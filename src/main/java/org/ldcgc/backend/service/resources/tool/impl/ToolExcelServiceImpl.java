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
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.excel.ResourceExcelMasterDto;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.common.EToolStatus;
import org.ldcgc.backend.util.common.EXlsxToolPos;
import org.ldcgc.backend.util.constants.Messages;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
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
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;

    private ResourceExcelMasterDto master;
    private Group group;

    public Map<String, Tool> excelToTools(MultipartFile excel, int initialRow) {
        processExcelArray();

        Map<String, Tool> tools = new HashMap<>();

        Integer groupId = Integer.valueOf(Optional.ofNullable(MDC.get("groupId")).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, Messages.Error.GROUP_NOT_FOUND_IN_TOKEN)));
        group = groupRepository.findById(groupId).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, String.format(Messages.Error.GROUP_NOT_FOUND, groupId)));

        Map<String, Integer> toolsMap = toolRepository.findAll().stream().collect(
            toMap(Tool::getBarcode, Tool::getId, (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        Map<String, Brand> brandsMap = brandRepository.findAll().stream().collect(
            toMap(Brand::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        Map<String, ResourceType> resourceTypesMap = resourceTypeRepository.findAll().stream().collect(
            toMap(ResourceType::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        Map<String, Location> locationsMap = locationRepository.findAll().stream().collect(
            toMap(Location::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheet("herramientas");

            master = ResourceExcelMasterDto.builder()
                .tools(toolsMap)
                .brands(brandsMap)
                .resourceTypes(resourceTypesMap)
                .locations(locationsMap)
                .build();

            for (int i = initialRow; i <= getLastRowByColumn(sheet, 3) ; i++)
                Optional.ofNullable(parseRowToTool(sheet.getRow(i))).ifPresent(t -> tools.put(t.getBarcode(), t));

        } catch (IOException e) {
            throw new RequestException(Messages.Error.EXCEL_PARSE_ERROR);
        }

        return tools;
    }

    private Tool parseRowToTool(Row row) {
        // MANDATORY. If this field is not present then don't proceed
        String name = getStringCellValue(row, EXlsxToolPos.NAME.getColumnNumber());
        if(StringUtils.isBlank(name)) return null;

        // barcode provided or generate random new
        String barcode = defaultIfBlank(
            getStringCellValue(row, EXlsxToolPos.BARCODE.getColumnNumber()),
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());

        Integer id = master.getTools().get(barcode);

        // resource type provided or set "sin especificar" as default
        String resourceType = defaultIfBlank(
            getStringCellValue(row, EXlsxToolPos.RESOURCE_TYPE.getColumnNumber()),
            "Sin especificar");
        if(isNotBlank(resourceType) && master.getResourceTypes().get(resourceType) == null) {
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(
                ResourceType.builder().name(resourceType).locked(false).build());
            master.getResourceTypes().put(newResourceType.getName(), newResourceType);
        }
        ResourceType resourceTypeDto = master.getResourceTypes().get(resourceType);

        // brand provided or set "sin marca" as default
        String brandName = defaultIfBlank(
            getStringCellValue(row, EXlsxToolPos.BRAND.getColumnNumber()),
            "Sin marca");
        if(master.getBrands().get(brandName) == null) {
            Brand newBrand = brandRepository.saveAndFlush(
                Brand.builder().name(brandName).locked(false).build());
            master.getBrands().put(newBrand.getName(), newBrand);
        }
        Brand brand = master.getBrands().get(brandName);

        String model = getStringCellValue(row, EXlsxToolPos.MODEL.getColumnNumber());

        String description = getStringCellValue(row, EXlsxToolPos.DESCRIPTION.getColumnNumber());

        String stockType = getStringCellValue(row, EXlsxToolPos.STOCK_WEIGHT_TYPE.getColumnNumber());

        Float weight = isNotBlank(stockType)
            ? getFloatCellValue(row, EXlsxToolPos.WEIGHT.getColumnNumber())
            : null;

        EStockType stockWeightType = isNotBlank(stockType)
            ? EStockType.getStockTypeByDesc(stockType)
            : EStockType.UNKNOWN;

        Float price = getFloatCellValue(row, EXlsxToolPos.PRICE.getColumnNumber());

        LocalDate purchaseDate = getDateCellValue(row, EXlsxToolPos.PURCHASE_DATE.getColumnNumber());

        String maintenanceFreq = getStringCellValue(row, EXlsxToolPos.MAINTENANCE_PERIOD.getColumnNumber());

        Integer maintenancePeriod = isNotBlank(maintenanceFreq)
            ? getIntegerCellValue(row, EXlsxToolPos.MAINTENANCE_FREQUENCY.getColumnNumber())
            : Integer.valueOf(0);

        ETimeUnit maintenanceTime = isNotBlank(maintenanceFreq)
            ? ETimeUnit.getTimeUnitByName(maintenanceFreq)
            : ETimeUnit.NEVER;

        LocalDate lastMaintenance = getDateCellValue(row, EXlsxToolPos.LAST_MAINTENANCE.getColumnNumber());
        if(lastMaintenance == null && !maintenanceTime.equals(ETimeUnit.NEVER))
            lastMaintenance = LocalDate.now();

        LocalDate nextMaintenance = lastMaintenance != null && lastMaintenance.equals(LocalDate.now())
            ? lastMaintenance.plus(Objects.requireNonNull(maintenancePeriod), maintenanceTime.getChronoUnit())
            : getDateCellValue(row, EXlsxToolPos.NEXT_MAINTENANCE.getColumnNumber());

        EToolStatus status = EToolStatus.getStatusByName(defaultIfBlank(getStringCellValue(row, EXlsxToolPos.STATUS.getColumnNumber()), "Disponible"));

        String locationName = defaultIfBlank(
            row.getCell(EXlsxToolPos.LOCATION.getColumnNumber()).getStringCellValue(),
            "Sin ubicación");
        Location location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(
                locationName, row.getRowNum() + 1, EXlsxToolPos.LOCATION.getColumnNumber() + 1,
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations()))));

        return Tool.builder()
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
