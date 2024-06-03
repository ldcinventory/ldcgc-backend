package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.util.common.EToolStatus;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.common.EXlsxToolPos;
import org.ldcgc.backend.util.constants.Messages;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getDateCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getFloatCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getIntegerCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getLastRowByColumn;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.processExcelArray;

@Component
@RequiredArgsConstructor
public class ToolExcelServiceImpl implements ToolExcelService {

    private final ToolRepository toolRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final LocationService locationService;

    private ToolExcelMasterDto master;

    public List<ToolDto> excelToTools(MultipartFile excel) {
        processExcelArray();

        List<ToolDto> tools = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheet("herramientas");
            master = ToolExcelMasterDto.builder()
                .tools(toolRepository.findAll().stream().map(ToolMapper.MAPPER::toDto)
                    .collect(toMap(ToolDto::getBarcode, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .brands(brandRepository.findAll().stream().map(BrandMapper.MAPPER::toDto)
                    .collect(toMap(BrandDto::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .resourceTypes(resourceTypeRepository.findAll().stream().map(ResourceTypeMapper.MAPPER::toDto)
                    .collect(toMap(ResourceTypeDto::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .locations(locationService.getAllLocations()
                    .stream().collect(toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .build();

            for (int i = 2; i <= getLastRowByColumn(sheet, 3) ; i++)
                Optional.ofNullable(parseRowToTool(sheet.getRow(i))).ifPresent(tools::add);

        } catch (IOException e) {
            throw new RequestException(Messages.Error.EXCEL_PARSE_ERROR);
        }

        return tools;
    }

    private ToolDto parseRowToTool(Row row) {
        // MANDATORY. If this field is not present then don't proceed
        String name = getStringCellValue(row, EXlsxToolPos.NAME.getColumnNumber());
        if(StringUtils.isBlank(name)) return null;

        // barcode provided or generate random new
        String barcode = StringUtils.defaultIfBlank(
            getStringCellValue(row, EXlsxToolPos.BARCODE.getColumnNumber()),
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());

        Integer id = Optional.ofNullable(master.getTools().get(barcode)).map(ToolDto::getId).orElse(null);

        // resource type provided or set "sin especificar" as default
        String resourceType = StringUtils.defaultIfBlank(
            getStringCellValue(row, EXlsxToolPos.RESOURCE_TYPE.getColumnNumber()),
            "Sin especificar");
        if(StringUtils.isNotBlank(resourceType) && master.getResourceTypes().get(resourceType) == null) {
            ResourceTypeDto newResourceTypeDto = ResourceTypeDto.builder().name(resourceType).locked(false).build();
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(ResourceTypeMapper.MAPPER.toEntity(newResourceTypeDto));
            master.getResourceTypes().put(newResourceType.getName(), ResourceTypeMapper.MAPPER.toDto(newResourceType));
        }
        ResourceTypeDto resourceTypeDto = master.getResourceTypes().get(resourceType);

        // brand provided or set "sin marca" as default
        String brandName = StringUtils.defaultIfBlank(
            getStringCellValue(row, EXlsxToolPos.BRAND.getColumnNumber()),
            "Sin marca");
        if(master.getBrands().get(brandName) == null) {
            BrandDto newBrandDto = BrandDto.builder().name(brandName).locked(false).build();
            Brand newBrand = brandRepository.saveAndFlush(BrandMapper.MAPPER.toEntity(newBrandDto));
            master.getBrands().put(newBrand.getName(), BrandMapper.MAPPER.toDto(newBrand));
        }
        BrandDto brand = master.getBrands().get(brandName);

        String model = getStringCellValue(row, EXlsxToolPos.MODEL.getColumnNumber());

        String description = getStringCellValue(row, EXlsxToolPos.DESCRIPTION.getColumnNumber());

        String stockType = getStringCellValue(row, EXlsxToolPos.STOCK_WEIGHT_TYPE.getColumnNumber());

        Float weight = StringUtils.isNotBlank(stockType)
            ? getFloatCellValue(row, EXlsxToolPos.WEIGHT.getColumnNumber())
            : Float.valueOf(1.0f);

        EStockType stockWeightType = StringUtils.isNotBlank(stockType)
            ? EStockType.getStockTypeByDesc(stockType)
            : EStockType.UNKNOWN;

        Float price = getFloatCellValue(row, EXlsxToolPos.PRICE.getColumnNumber());

        LocalDate purchaseDate = getDateCellValue(row, EXlsxToolPos.PURCHASE_DATE.getColumnNumber());

        String maintenanceFreq = getStringCellValue(row, EXlsxToolPos.MAINTENANCE_PERIOD.getColumnNumber());

        Integer maintenancePeriod = StringUtils.isNotBlank(maintenanceFreq)
            ? getIntegerCellValue(row, EXlsxToolPos.MAINTENANCE_FREQUENCY.getColumnNumber())
            : Integer.valueOf(0);

        ETimeUnit maintenanceTime = StringUtils.isNotBlank(maintenanceFreq)
            ? ETimeUnit.getTimeUnitByName(maintenanceFreq)
            : ETimeUnit.NEVER;

        LocalDate lastMaintenance = getDateCellValue(row, EXlsxToolPos.LAST_MAINTENANCE.getColumnNumber());
        if(lastMaintenance == null && !maintenanceTime.equals(ETimeUnit.NEVER))
            lastMaintenance = LocalDate.now();

        LocalDate nextMaintenance = lastMaintenance != null && lastMaintenance.equals(LocalDate.now())
            ? lastMaintenance.plus(Objects.requireNonNull(maintenancePeriod), maintenanceTime.getChronoUnit())
            : getDateCellValue(row, EXlsxToolPos.NEXT_MAINTENANCE.getColumnNumber());

        EToolStatus status = EToolStatus.getStatusByName(StringUtils.defaultIfBlank(getStringCellValue(row, EXlsxToolPos.STATUS.getColumnNumber()), "Disponible"));

        String locationName = StringUtils.defaultIfBlank(
            row.getCell(EXlsxToolPos.LOCATION.getColumnNumber()).getStringCellValue(),
            "Sin ubicación");
        LocationDto location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(
                locationName, row.getRowNum() + 1, EXlsxToolPos.LOCATION.getColumnNumber() + 1,
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations().values().stream().map(LocationDto::getName).toList()))));

        Integer groupId = Integer.valueOf(Optional.ofNullable(MDC.get("groupId")).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, Messages.Error.GROUP_NOT_FOUND_IN_TOKEN)));
        GroupDto group = GroupDto.builder().id(groupId).build();

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
