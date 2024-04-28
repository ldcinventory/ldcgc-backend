package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
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
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.util.common.EStockType;
import org.ldcgc.backend.util.common.EXlsxConsumablePos;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;
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
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringArrayCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getStringCellValue;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.processExcelArray;

@Component
@RequiredArgsConstructor
public class ConsumableExcelServiceImpl implements ConsumableExcelService {

    private final ConsumableRepository consumableRepository;
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final LocationService locationService;
    private final GroupService groupService;

    private ConsumableExcelMasterDto master;

    public List<ConsumableDto> excelToConsumables(MultipartFile excel) {
        processExcelArray();

        List<ConsumableDto> consumables = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            master = ConsumableExcelMasterDto.builder()
                .consumables(consumableRepository.findAll().stream()
                    .map(ConsumableMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(ConsumableDto::getBarcode, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .brands(brandRepository.findAll().stream().map(BrandMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(BrandDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .resourceTypes(resourceTypeRepository.findAll().stream().map(ResourceTypeMapper.MAPPER::toDto)
                    .collect(Collectors.toMap(ResourceTypeDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .locations(locationService.getAllLocations()
                    .stream().collect(Collectors.toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .groups(groupService.getAllGroups()
                    .stream().collect(Collectors.toMap(GroupDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .build();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                consumables.add(parseRowToTool(sheet.getRow(i)));
            }

        } catch (IOException e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.EXCEL_PARSE_ERROR);
        }

        return consumables;
    }

    private ConsumableDto parseRowToTool(Row row) {
        String barcode = getStringCellValue(row, EXlsxConsumablePos.BARCODE.getColumnNumber());

        Integer id = Optional.ofNullable(master.getConsumables().get(barcode)).map(ConsumableDto::getId).orElse(null);

        String resourceType = getStringCellValue(row, EXlsxConsumablePos.RESOURCE_TYPE.getColumnNumber());
        if(master.getResourceTypes().get(resourceType) == null) {
            ResourceTypeDto newResourceTypeDto = ResourceTypeDto.builder().name(resourceType).locked(false).build();
            ResourceType newResourceType = resourceTypeRepository.saveAndFlush(ResourceTypeMapper.MAPPER.toEntity(newResourceTypeDto));
            master.getResourceTypes().put(newResourceType.getName(), ResourceTypeMapper.MAPPER.toDto(newResourceType));
        }
        ResourceTypeDto resourceTypeDto = master.getResourceTypes().get(resourceType);

        String brandName = getStringCellValue(row, EXlsxConsumablePos.BRAND.getColumnNumber());
        if(master.getBrands().get(brandName) == null) {
            BrandDto newBrandDto = BrandDto.builder().name(brandName).locked(false).build();
            Brand newBrand = brandRepository.saveAndFlush(BrandMapper.MAPPER.toEntity(newBrandDto));
            master.getBrands().put(newBrand.getName(), BrandMapper.MAPPER.toDto(newBrand));
        }
        BrandDto brand = master.getBrands().get(brandName);

        String name = getStringCellValue(row, EXlsxConsumablePos.NAME.getColumnNumber());

        String model = getStringCellValue(row, EXlsxConsumablePos.MODEL.getColumnNumber());

        String description = getStringCellValue(row, EXlsxConsumablePos.DESCRIPTION.getColumnNumber());

        Float price = getFloatCellValue(row, EXlsxConsumablePos.PRICE.getColumnNumber());

        LocalDate purchaseDate = getDateCellValue(row, EXlsxConsumablePos.PURCHASE_DATE.getColumnNumber());

        String[] urlImages = getStringArrayCellValue(row, EXlsxConsumablePos.URL_IMAGES.getColumnNumber());

        Float quantityEachItem = getFloatCellValue(row, EXlsxConsumablePos.QTY_EACH_ITEM.getColumnNumber());

        Float stock = getFloatCellValue(row, EXlsxConsumablePos.STOCK.getColumnNumber());

        Float minStock = getFloatCellValue(row, EXlsxConsumablePos.MIN_STOCK.getColumnNumber());

        EStockType stockType = EStockType.getStockTypeByName(getStringCellValue(row, EXlsxConsumablePos.STOCK_TYPE.getColumnNumber()));

        String locationName = row.getCell(EXlsxConsumablePos.LOCATION.getColumnNumber()).getStringCellValue();
        LocationDto location = Optional.ofNullable(master.getLocations().get(locationName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(locationName, row.getRowNum(), EXlsxConsumablePos.LOCATION.getColumnNumber(),
                Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.getLocations().values().stream().map(LocationDto::getName).toList()))));

        String groupName = getStringCellValue(row, EXlsxConsumablePos.GROUP.getColumnNumber());
        GroupDto group = Optional.ofNullable(master.getGroups().get(groupName))
            .orElseThrow(() -> new RequestException(generateExcelErrorMessage(groupName, row.getRowNum(), EXlsxConsumablePos.GROUP.getColumnNumber(),
                Messages.Error.GROUP_NOT_FOUND_EXCEL.formatted(groupName, master.getGroups().values().stream().map(GroupDto::getName).toList()))));

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
            .urlImages(urlImages)
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

