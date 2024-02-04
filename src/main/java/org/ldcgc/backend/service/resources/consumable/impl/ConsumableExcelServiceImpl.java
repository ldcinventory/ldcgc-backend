package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelMasterDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.util.common.EExcelConsumablePositions;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ldcgc.backend.util.conversion.Convert.convertToFloat2Decimals;
import static org.ldcgc.backend.util.conversion.Convert.stringToLocalDate;

@Component
@RequiredArgsConstructor
public class ConsumableExcelServiceImpl implements ConsumableExcelService {

    private final ConsumableRepository consumableRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final GroupsService groupsService;

    public List<ConsumableDto> excelToConsumable(MultipartFile excel) {
        List<ConsumableDto> consumable = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            ConsumableExcelMasterDto master = ConsumableExcelMasterDto.builder()
                    .consumable(consumableRepository.findAll().stream().map(ConsumableMapper.MAPPER::toDto)
                            .collect(Collectors.toMap(ConsumableDto::getBarcode, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                    .brands(categoryService.getCategoryParent(CategoryParentEnum.BRANDS).getCategories().stream()
                            .collect(Collectors.toMap(CategoryDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                    .categories(categoryService.getCategoryParent(CategoryParentEnum.CATEGORIES).getCategories()
                            .stream().collect(Collectors.toMap(CategoryDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                    .locations(locationService.getAllLocations()
                            .stream().collect(Collectors.toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                    .groups(groupsService.getAllGroups()
                            .stream().collect(Collectors.toMap(GroupDto::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                    .build();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                consumable.add(parseRowToTool(sheet.getRow(i), master));
            }

        } catch (IOException e) {
            throw new RequestException(Messages.Error.EXCEL_PARSE_ERROR);
        }

        return consumable;
    }

    private ConsumableDto parseRowToTool(Row row, ConsumableExcelMasterDto master) {
        String barcode = getStringCellValue(row, EExcelConsumablePositions.BARCODE.getColumnNumber());
        Integer id = Optional.ofNullable(master.consumable.get(barcode)).map(ConsumableDto::getId).orElse(null);

        String brandName = getStringCellValue(row, EExcelConsumablePositions.BRAND.getColumnNumber());
        CategoryDto brand = Optional.ofNullable(master.brands.get(brandName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(brandName, row.getRowNum(), EExcelConsumablePositions.BRAND.getColumnNumber(), Messages.Error.CATEGORY_SON_NOT_FOUND
                        .formatted(CategoryParentEnum.BRANDS.getName(), brandName, CategoryParentEnum.BRANDS.getName(), master.brands.values().stream().map(CategoryDto::getName).toList().toString()))));
        String categoryName = getStringCellValue(row, EExcelConsumablePositions.CATEGORY.getColumnNumber());
        CategoryDto category = Optional.ofNullable(master.categories.get(categoryName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(categoryName, row.getRowNum(), EExcelConsumablePositions.CATEGORY.getColumnNumber(),
                        Messages.Error.CATEGORY_SON_NOT_FOUND
                                .formatted(CategoryParentEnum.CATEGORIES.getName(), categoryName, CategoryParentEnum.CATEGORIES.getName(), master.categories.values().stream().map(CategoryDto::getName).toList().toString()))));

        String locationName = row.getCell(EExcelConsumablePositions.LOCATION.getColumnNumber()).getStringCellValue();
        LocationDto location = Optional.ofNullable(master.locations.get(locationName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(locationName, row.getRowNum(), EExcelConsumablePositions.LOCATION.getColumnNumber(),
                        Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.locations.values().stream().map(LocationDto::getName).toList()))));

        String groupName = getStringCellValue(row, EExcelConsumablePositions.GROUP.getColumnNumber());
        GroupDto group = Optional.ofNullable(master.groups.get(groupName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(groupName, row.getRowNum(), EExcelConsumablePositions.GROUP.getColumnNumber(),
                        Messages.Error.GROUP_NOT_FOUND_EXCEL.formatted(groupName, master.groups.values().stream().map(GroupDto::getName).toList()))));

        return ConsumableDto.builder()
                .id(id)
                .barcode(barcode)
                .name(getStringCellValue(row, EExcelConsumablePositions.NAME.getColumnNumber()))
                .brand(brand)
                .model(getStringCellValue(row, EExcelConsumablePositions.MODEL.getColumnNumber()))
                .category(category)
                .name(getStringCellValue(row, EExcelConsumablePositions.NAME.getColumnNumber()))
                .description(getStringCellValue(row, EExcelConsumablePositions.DESCRIPTION.getColumnNumber()))
                .urlImages(getStringCellValue(row, EExcelConsumablePositions.URL_IMAGES.getColumnNumber()))
                .location(location)
                .minStock(getIntegerCellValue(row, EExcelConsumablePositions.MIN_STOCK.getColumnNumber()))
                .price(getFolatCellValue(row, EExcelConsumablePositions.PRICE.getColumnNumber()))
                .purchase_date(getDateCellValue(row, EExcelConsumablePositions.PURCHASE_DATE.getColumnNumber()))
                .stock(getIntegerCellValue(row, EExcelConsumablePositions.STOCK.getColumnNumber()))
                .group(group)
                .minStock(getIntegerCellValue(row, EExcelConsumablePositions.MIN_STOCK.getColumnNumber()))
                .build();
    }

    private String generateExcelErrorMessage(String value, Integer row, Integer column, String message) {
        return Messages.Error.EXCEL_VALUE_INCORRECT.formatted(value, row, column).concat("\n").concat(message);
    }

    private String getStringCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.STRING))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return cell.getStringCellValue();
    }

    private Integer getIntegerCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.NUMERIC))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.NUMERIC.toString()));

        return (int) cell.getNumericCellValue();
    }

    private LocalDate getDateCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();
        String cellValue = cell.getStringCellValue() ;

        if (!cellType.equals(CellType.STRING))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return stringToLocalDate(cellValue, "yyyy-MM-dd");//cell.getLocalDateTimeCellValue().toLocalDate();
    }
    private Float getFolatCellValue(Row row, Integer columnNumber){
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();
        if (!cellType.equals(CellType.STRING))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return convertToFloat2Decimals(cell.getStringCellValue());
    }
}

