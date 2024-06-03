package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelMasterDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.category.BrandMapper;
import org.ldcgc.backend.payload.mapper.category.ResourceTypeMapper;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.EXlsxConsumablePos;
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
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getDateCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getFloatCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getLastRowByColumn;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.processExcelArray;

@Component
@RequiredArgsConstructor
public class ConsumableExcelServiceImpl implements ConsumableExcelService {

    private final ConsumableRepository consumableRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final LocationService locationService;
    private final GroupRepository groupRepository;

    private ConsumableExcelMasterDto master;

    public List<ConsumableDto> excelToConsumables(MultipartFile excel) {
        processExcelArray();

        List<ConsumableDto> consumables = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheet("consumibles");
            master = ConsumableExcelMasterDto.builder()
                .consumables(consumableRepository.findAll().stream()
                    .map(ConsumableMapper.MAPPER::toDto)
                    .collect(toMap(ConsumableDto::getBarcode, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .brands(brandRepository.findAll().stream()
                    .map(BrandMapper.MAPPER::toDto)
                    .collect(toMap(BrandDto::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .resourceTypes(resourceTypeRepository.findAll().stream()
                    .map(ResourceTypeMapper.MAPPER::toDto)
                    .collect(toMap(ResourceTypeDto::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .locations(locationService.getAllLocations()
                    .stream().collect(toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing, LinkedCaseInsensitiveMap::new)))
                .build();

            for (int i = 2; i <= getLastRowByColumn(sheet, 3); i++)
                Optional.ofNullable(parseRowToTool(sheet.getRow(i))).ifPresent(consumables::add);

        } catch (IOException e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.EXCEL_PARSE_ERROR);
        }

        return consumables;
    }

    private ConsumableDto parseRowToTool(Row row) {
        // MANDATORY. If this field is not present then don't proceed
        String name = getStringCellValue(row, EXlsxConsumablePos.NAME.getColumnNumber());
        if(StringUtils.isBlank(name)) return null;

        // barcode provided or generate random new
        String barcode = StringUtils.defaultIfBlank(
            getStringCellValue(row, EXlsxConsumablePos.BARCODE.getColumnNumber()),
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());

        Integer id = Optional.ofNullable(master.getConsumables().get(barcode)).map(ConsumableDto::getId).orElse(null);

        // resource type provided or set "sin especificar" as default
        String resourceType = StringUtils.defaultIfBlank(
            getStringCellValue(row, EXlsxConsumablePos.RESOURCE_TYPE.getColumnNumber()),
            "Sin especificar");
        if(StringUtils.isNotBlank(resourceType) && master.getResourceTypes().get(resourceType) == null) {
            ResourceTypeDto newResourceTypeDto = ResourceTypeDto.builder().name(resourceType).locked(false).build();
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(ResourceTypeMapper.MAPPER.toEntity(newResourceTypeDto));
            master.getResourceTypes().put(newResourceType.getName(), ResourceTypeMapper.MAPPER.toDto(newResourceType));
        }
        ResourceTypeDto resourceTypeDto = master.getResourceTypes().get(resourceType);

        // brand provided or set "sin marca" as default
        String brandName = StringUtils.defaultIfBlank(
            getStringCellValue(row, EXlsxConsumablePos.BRAND.getColumnNumber()),
            "Sin marca");
        if(StringUtils.isNotBlank(brandName) && master.getBrands().get(brandName) == null) {
            BrandDto newBrandDto = BrandDto.builder().name(brandName).locked(false).build();
            Brand newBrand = brandRepository.saveAndFlush(BrandMapper.MAPPER.toEntity(newBrandDto));
            master.getBrands().put(newBrand.getName(), BrandMapper.MAPPER.toDto(newBrand));
        }
        BrandDto brand = master.getBrands().get(brandName);

        String model = getStringCellValue(row, EXlsxConsumablePos.MODEL.getColumnNumber());

        String description = getStringCellValue(row, EXlsxConsumablePos.DESCRIPTION.getColumnNumber());

        Float price = getFloatCellValue(row, EXlsxConsumablePos.PRICE.getColumnNumber());

        LocalDate purchaseDate = getDateCellValue(row, EXlsxConsumablePos.PURCHASE_DATE.getColumnNumber());

        Float quantityEachItem = ObjectUtils.defaultIfNull(getFloatCellValue(row, EXlsxConsumablePos.QTY_EACH_ITEM.getColumnNumber()), 1.0f);

        Float stock = ObjectUtils.defaultIfNull(getFloatCellValue(row, EXlsxConsumablePos.STOCK.getColumnNumber()), 1.0f);

        Float minStock = ObjectUtils.defaultIfNull(getFloatCellValue(row, EXlsxConsumablePos.MIN_STOCK.getColumnNumber()), 1.0f);

        EStockType stockType = EStockType.getStockTypeByDesc(
            StringUtils.defaultIfBlank(getStringCellValue(row, EXlsxConsumablePos.STOCK_TYPE.getColumnNumber()),
            "unidades"));

        String locationName = StringUtils.defaultIfBlank(
            row.getCell(EXlsxConsumablePos.LOCATION.getColumnNumber()).getStringCellValue(),
            "Sin ubicación");
        LocationDto location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(
                locationName, row.getRowNum() + 1, EXlsxConsumablePos.LOCATION.getColumnNumber() + 1,
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations().values().stream().map(LocationDto::getName).toList()))));

        Integer groupId = Integer.valueOf(Optional.ofNullable(MDC.get("groupId")).orElseThrow(() ->
            new RequestException(HttpStatus.FORBIDDEN, Messages.Error.GROUP_NOT_FOUND_IN_TOKEN)));
        GroupDto group = GroupDto.builder().id(groupId).build();

        return ConsumableDto.builder()
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

