package org.ldcgc.backend.service.resources.consumable.impl;

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
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.app.exception.RequestException;
import org.ldcgc.backend.payload.dto.excel.ResourceExcelMasterDto;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.shared.enums.EStockType;
import org.ldcgc.backend.shared.enums.EXlsxConsumablePos;
import org.ldcgc.backend.shared.constants.Messages;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ldcgc.backend.shared.conversion.ExcelFunctions.getDateCellValue;
import static org.ldcgc.backend.shared.conversion.ExcelFunctions.getFloatCellValue;
import static org.ldcgc.backend.shared.conversion.ExcelFunctions.getLastRowByColumn;
import static org.ldcgc.backend.shared.conversion.ExcelFunctions.getStringCellValue;
import static org.ldcgc.backend.shared.conversion.ExcelFunctions.processExcelArray;

@Component
@RequiredArgsConstructor
public class ConsumableExcelServiceImpl implements ConsumableExcelService {

    private final ConsumableRepository consumableRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;

    private ResourceExcelMasterDto master;
    private Group group;

    public Map<String, Consumable> excelToConsumables(MultipartFile excel, int initialRow) {
        processExcelArray();

        Map<String, Consumable> consumables = new HashMap<>();

        Integer groupId = Integer.valueOf(Optional.ofNullable(MDC.get("groupId")).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, Messages.Error.GROUP_NOT_FOUND_IN_TOKEN)));
        group = groupRepository.findById(groupId).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, String.format(Messages.Error.GROUP_NOT_FOUND, groupId)));

        Map<String, Integer> consumablesMap = consumableRepository.findAll().stream().collect(
            toMap(Consumable::getBarcode, Consumable::getId, (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        Map<String, Brand> brandsMap = brandRepository.findAll().stream().collect(
            toMap(Brand::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        Map<String, ResourceType> resourceTypesMap = resourceTypeRepository.findAll().stream().collect(
            toMap(ResourceType::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        Map<String, Location> locationsMap = locationRepository.findAll().stream().collect(
            toMap(Location::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new));

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheet("consumibles");
            master = ResourceExcelMasterDto.builder()
                .consumables(consumablesMap)
                .brands(brandsMap)
                .resourceTypes(resourceTypesMap)
                .locations(locationsMap)
                .build();

            for (int i = initialRow; i <= getLastRowByColumn(sheet, 3); i++)
                Optional.ofNullable(parseRowToTool(sheet.getRow(i))).ifPresent(c -> consumables.put(c.getBarcode(), c));

        } catch (IOException e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.EXCEL_PARSE_ERROR);
        }

        return consumables;
    }

    private Consumable parseRowToTool(Row row) {
        // barcode provided or generate random new, then check in master to avoid duplicities
        String barcode = defaultIfBlank(
            getStringCellValue(row, EXlsxConsumablePos.BARCODE.getColumnNumber()),
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        if(master.getConsumables().get(barcode) != null)
            return null;

        Integer id = master.getConsumables().get(barcode);

        // MANDATORY. If this field is not present then don't proceed
        String name = getStringCellValue(row, EXlsxConsumablePos.NAME.getColumnNumber());
        if(StringUtils.isBlank(name)) return null;

        // resource type provided or set "sin especificar" as default
        String resourceType = defaultIfBlank(
            getStringCellValue(row, EXlsxConsumablePos.RESOURCE_TYPE.getColumnNumber()),
            "Sin especificar");
        if(isNotBlank(resourceType) && master.getResourceTypes().get(resourceType) == null) {
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(
                ResourceType.builder().name(resourceType).locked(false).build());
            master.getResourceTypes().put(newResourceType.getName(), newResourceType);
        }
        ResourceType resourceTypeDto = master.getResourceTypes().get(resourceType);

        // brand provided or set "sin marca" as default
        String brandName = defaultIfBlank(
            getStringCellValue(row, EXlsxConsumablePos.BRAND.getColumnNumber()),
            "Sin marca");
        if(isNotBlank(brandName) && master.getBrands().get(brandName) == null) {
            Brand newBrand = brandRepository.saveAndFlush(
                Brand.builder().name(brandName).locked(false).build());
            master.getBrands().put(newBrand.getName(), newBrand);
        }
        Brand brand = master.getBrands().get(brandName);

        String model = getStringCellValue(row, EXlsxConsumablePos.MODEL.getColumnNumber());

        String description = getStringCellValue(row, EXlsxConsumablePos.DESCRIPTION.getColumnNumber());

        Float price = getFloatCellValue(row, EXlsxConsumablePos.PRICE.getColumnNumber());

        LocalDate purchaseDate = getDateCellValue(row, EXlsxConsumablePos.PURCHASE_DATE.getColumnNumber());

        Float quantityEachItem = defaultIfNull(getFloatCellValue(row, EXlsxConsumablePos.QTY_EACH_ITEM.getColumnNumber()), 1.0f);

        Float stock = defaultIfNull(getFloatCellValue(row, EXlsxConsumablePos.STOCK.getColumnNumber()), 1.0f);

        Float minStock = defaultIfNull(getFloatCellValue(row, EXlsxConsumablePos.MIN_STOCK.getColumnNumber()), 1.0f);

        EStockType stockType = EStockType.getStockTypeByDesc(
            defaultIfBlank(getStringCellValue(row, EXlsxConsumablePos.STOCK_TYPE.getColumnNumber()),
            "unidades"));

        String locationName = defaultIfBlank(
            row.getCell(EXlsxConsumablePos.LOCATION.getColumnNumber()).getStringCellValue(),
            "Sin ubicación");
        Location location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(
                locationName, row.getRowNum() + 1, EXlsxConsumablePos.LOCATION.getColumnNumber() + 1,
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations().values().stream().map(Location::getName).toList()))));

        return Consumable.builder()
            .id(id)
            .barcode(barcode)
            .resourceType(resourceTypeDto)
            .brand(brand)
            .name(name)
            .model(model)
            .description(description)
            .price(price)
            .purchaseDate(purchaseDate)
            .quantityEachItem(quantityEachItem)
            .stock(stock)
            .minStock(minStock)
            .stockType(stockType)
            .location(location)
            .group(group)
            .build();
    }

    private String generateExcelErrorMessage(String value, Integer row, Integer column, String message) {
        return Messages.Error.EXCEL_VALUE_INCORRECT.formatted(value, row, column).concat(". ").concat(message);
    }

}
