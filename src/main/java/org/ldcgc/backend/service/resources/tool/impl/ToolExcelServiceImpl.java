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
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.common.EXlsxToolPos;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ldcgc.backend.util.conversion.ExcelFunctions.getDateCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getFloatCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getIntegerCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringArrayCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.processExcelArray;

@Component
@RequiredArgsConstructor
public class ToolExcelServiceImpl implements ToolExcelService {

    private final ToolRepository toolRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final LocationService locationService;
    private final GroupService groupService;

    private ToolExcelMasterDto master;

    public List<ToolDto> excelToTools(MultipartFile excel) {
        processExcelArray();

        List<ToolDto> tools = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            master = ToolExcelMasterDto.builder()
                .tools(toolRepository.findAll().stream().map(ToolMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(ToolDto::getBarcode, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .brands(brandRepository.findAll().stream().map(BrandMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(BrandDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .resourceTypes(resourceTypeRepository.findAll().stream().map(ResourceTypeMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(ResourceTypeDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .locations(locationService.getAllLocations()
                    .stream().collect(Collectors.toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .groups(groupService.getAllGroups()
                    .stream().collect(Collectors.toMap(GroupDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .build();

            for (int i = 1; i <= sheet.getLastRowNum(); i++)
                tools.add(parseRowToTool(sheet.getRow(i)));

        } catch (IOException e) {
            throw new RequestException(Messages.Error.EXCEL_PARSE_ERROR);
        }

        return tools;
    }

    private ToolDto parseRowToTool(Row row) {
        String barcode = getStringCellValue(row, EXlsxToolPos.BARCODE.getColumnNumber());

        Integer id = Optional.ofNullable(master.getTools().get(barcode)).map(ToolDto::getId).orElse(null);

        String resourceType = getStringCellValue(row, EXlsxToolPos.RESOURCE_TYPE.getColumnNumber());
        if(master.getResourceTypes().get(resourceType) == null) {
            ResourceTypeDto newResourceTypeDto = ResourceTypeDto.builder().name(resourceType).locked(false).build();
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(ResourceTypeMapper.MAPPER.toEntity(newResourceTypeDto));
            master.getResourceTypes().put(newResourceType.getName(), ResourceTypeMapper.MAPPER.toDto(newResourceType));
        }
        ResourceTypeDto resourceTypeDto = master.getResourceTypes().get(resourceType);

        String brandName = getStringCellValue(row, EXlsxToolPos.BRAND.getColumnNumber());
        if(master.getBrands().get(brandName) == null) {
            BrandDto newBrandDto = BrandDto.builder().name(brandName).locked(false).build();
            Brand newBrand = brandRepository.saveAndFlush(BrandMapper.MAPPER.toEntity(newBrandDto));
            master.getBrands().put(newBrand.getName(), BrandMapper.MAPPER.toDto(newBrand));
        }
        BrandDto brand = master.getBrands().get(brandName);

        String name = getStringCellValue(row, EXlsxToolPos.NAME.getColumnNumber());

        String model = getStringCellValue(row, EXlsxToolPos.MODEL.getColumnNumber());

        String description = getStringCellValue(row, EXlsxToolPos.DESCRIPTION.getColumnNumber());

        Float weight = getFloatCellValue(row, EXlsxToolPos.WEIGHT.getColumnNumber());

        EStockType stockWeightType = EStockType.getStockTypeByDesc(getStringCellValue(row, EXlsxToolPos.STOCK_WEIGHT_TYPE.getColumnNumber()));

        Float price = getFloatCellValue(row, EXlsxToolPos.PRICE.getColumnNumber());

        LocalDate purchaseDate = getDateCellValue(row, EXlsxToolPos.PURCHASE_DATE.getColumnNumber());

        String[] urlImages = getStringArrayCellValue(row, EXlsxToolPos.URL_IMAGES.getColumnNumber());

        Integer maintenancePeriod = getIntegerCellValue(row, EXlsxToolPos.MAINTENANCE_PERIOD.getColumnNumber());

        ETimeUnit maintenanceTime = ETimeUnit.getTimeUnitByName(getStringCellValue(row, EXlsxToolPos.MAINTENANCE_TIME.getColumnNumber()));

        LocalDate lastMaintenance = getDateCellValue(row, EXlsxToolPos.LAST_MAINTENANCE.getColumnNumber());

        LocalDate nextMaintenance = getDateCellValue(row, EXlsxToolPos.NEXT_MAINTENANCE.getColumnNumber());

        EStatus status = EStatus.getStatusByName(getStringCellValue(row, EXlsxToolPos.STATUS.getColumnNumber()));

        String locationName = row.getCell(EXlsxToolPos.LOCATION.getColumnNumber()).getStringCellValue();
        LocationDto location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(locationName, row.getRowNum(), EXlsxToolPos.LOCATION.getColumnNumber(),
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations().values().stream().map(LocationDto::getName).toList()))));

        String groupName = getStringCellValue(row, EXlsxToolPos.GROUP.getColumnNumber());
        GroupDto group = Optional.ofNullable(master.getGroups().get(groupName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(groupName, row.getRowNum(), EXlsxToolPos.GROUP.getColumnNumber(),
                Messages.Error.GROUP_NOT_FOUND_EXCEL.formatted(groupName, master.getGroups().values().stream().map(GroupDto::getName).toList()))));

        return ToolDto.builder()
            .id(id)
            .barcode(barcode)
            .name(name)
            .brand(brand)
            .model(model)
            .resourceType(resourceTypeDto)
            .description(description)
            .weight(weight)
            .stockWeightType(stockWeightType)
            .price(price)
            .purchaseDate(purchaseDate)
            .urlImages(urlImages)
            .maintenancePeriod(maintenancePeriod)
            .maintenanceTime(maintenanceTime)
            .lastMaintenance(lastMaintenance)
            .nextMaintenance(nextMaintenance)
            .status(status)
            .location(location)
            .group(group)
            .build();
    }

    private String generateExcelErrorMessage(String value, Integer row, Integer column, String message) {
        return Messages.Error.EXCEL_VALUE_INCORRECT.formatted(value, row, column).concat("\n").concat(message);
    }

}
