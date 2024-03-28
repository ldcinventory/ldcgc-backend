package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.excel.ToolExcelMasterDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.category.BrandMapper;
import org.ldcgc.backend.payload.mapper.category.ResourceTypeMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.util.common.EExcelToolsPositions;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ldcgc.backend.util.conversion.ExcelFunctions.getDateCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getIntegerCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringArrayCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringCellValue;

@Component
@RequiredArgsConstructor
public class ToolExcelServiceImpl implements ToolExcelService {

    private final ToolRepository toolRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final LocationService locationService;
    private final GroupsService groupsService;

    public List<ToolDto> excelToTools(MultipartFile excel) {
        List<ToolDto> tools = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            ToolExcelMasterDto master = ToolExcelMasterDto.builder()
                .tools(toolRepository.findAll().stream().map(ToolMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(ToolDto::getBarcode, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .brands(brandRepository.findAll().stream().map(BrandMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(BrandDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .resourceTypes(resourceTypeRepository.findAll().stream().map(ResourceTypeMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(ResourceTypeDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .locations(locationService.getAllLocations()
                    .stream().collect(Collectors.toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .groups(groupsService.getAllGroups()
                    .stream().collect(Collectors.toMap(GroupDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .build();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                tools.add(parseRowToTool(sheet.getRow(i), master));
            }

        } catch (IOException e) {
            throw new RequestException(Messages.Error.EXCEL_PARSE_ERROR);
        }

        return tools;
    }

    private ToolDto parseRowToTool(Row row, ToolExcelMasterDto master) {
        String barcode = getStringCellValue(row, EExcelToolsPositions.BARCODE.getColumnNumber());

        Integer id = Optional.ofNullable(master.getTools().get(barcode)).map(ToolDto::getId).orElse(null);

        String brandName = getStringCellValue(row, EExcelToolsPositions.BRAND.getColumnNumber());
        if(master.getBrands().get(brandName) == null) {
            BrandDto newBrandDto = BrandDto.builder().name(brandName).locked(false).build();
            Brand newBrand = brandRepository.saveAndFlush(BrandMapper.MAPPER.toEntity(newBrandDto));
            master.getBrands().put(newBrand.getName(), BrandMapper.MAPPER.toDto(newBrand));
        }
        BrandDto brand = master.getBrands().get(brandName);

        String resourceType = getStringCellValue(row, EExcelToolsPositions.RESOURCE_TYPE.getColumnNumber());
        if(master.getResourceTypes().get(resourceType) == null) {
            ResourceTypeDto newResourceTypeDto = ResourceTypeDto.builder().name(resourceType).locked(false).build();
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(ResourceTypeMapper.MAPPER.toEntity(newResourceTypeDto));
            master.getResourceTypes().put(newResourceType.getName(), ResourceTypeMapper.MAPPER.toDto(newResourceType));
        }
        ResourceTypeDto resourceTypeDto = master.getResourceTypes().get(resourceType);

        EStatus status = EStatus.getStatusByName(getStringCellValue(row, EExcelToolsPositions.STATUS.getColumnNumber()));

        String locationName = row.getCell(EExcelToolsPositions.LOCATION.getColumnNumber()).getStringCellValue();
        LocationDto location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(locationName, row.getRowNum(), EExcelToolsPositions.LOCATION.getColumnNumber(),
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations().values().stream().map(LocationDto::getName).toList()))));

        ETimeUnit maintenanceTime = ETimeUnit.getTimeUnitByName(getStringCellValue(row, EExcelToolsPositions.MAINTENANCE_TIME.getColumnNumber()));

        String groupName = getStringCellValue(row, EExcelToolsPositions.GROUP.getColumnNumber());
        GroupDto group = Optional.ofNullable(master.getGroups().get(groupName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(groupName, row.getRowNum(), EExcelToolsPositions.GROUP.getColumnNumber(),
                Messages.Error.GROUP_NOT_FOUND_EXCEL.formatted(groupName, master.getGroups().values().stream().map(GroupDto::getName).toList()))));

        return ToolDto.builder()
            .id(id)
            .barcode(barcode)
            .name(getStringCellValue(row, EExcelToolsPositions.NAME.getColumnNumber()))
            .brand(brand)
            .model(getStringCellValue(row, EExcelToolsPositions.MODEL.getColumnNumber()))
            .resourceType(resourceTypeDto)
            .description(getStringCellValue(row, EExcelToolsPositions.DESCRIPTION.getColumnNumber()))
            .urlImages(getStringArrayCellValue(row, EExcelToolsPositions.URL_IMAGES.getColumnNumber()))
            .status(status)
            .location(location)
            .maintenancePeriod(getIntegerCellValue(row, EExcelToolsPositions.MAINTENANCE_PERIOD.getColumnNumber()))
            .maintenanceTime(maintenanceTime)
            .lastMaintenance(getDateCellValue(row, EExcelToolsPositions.LAST_MAINTENANCE.getColumnNumber()))
            .group(group)
            .build();
    }

    private String generateExcelErrorMessage(String value, Integer row, Integer column, String message) {
        return Messages.Error.EXCEL_VALUE_INCORRECT.formatted(value, row, column).concat("\n").concat(message);
    }

}
