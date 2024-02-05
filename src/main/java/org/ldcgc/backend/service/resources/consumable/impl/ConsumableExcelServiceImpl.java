package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelMasterDto;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.util.common.EExcelConsumablesPositions;
import org.ldcgc.backend.util.retrieving.Messages;
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

import static org.ldcgc.backend.util.conversion.Convert.convertToFloat2Decimals;
import static org.ldcgc.backend.util.conversion.Convert.stringToLocalDate;

@Component
@RequiredArgsConstructor
public class ConsumableExcelServiceImpl implements ConsumableExcelService {

    private final ConsumableRepository consumableRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;

    public List<Consumable> excelToConsumables(MultipartFile excel) {
        List<Consumable> consumable = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            ConsumableExcelMasterDto master = ConsumableExcelMasterDto.builder()
                .consumable(consumableRepository.findAll().stream()
                    .collect(Collectors.toMap(Consumable::getBarcode, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .brands(categoryRepository.findAllByParent_Name(CategoryParentEnum.BRANDS.getBbddName()).stream()
                    .collect(Collectors.toMap(Category::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .categories(categoryRepository.findAllByParent_Name(CategoryParentEnum.BRANDS.getBbddName()).stream()
                    .collect(Collectors.toMap(Category::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .locations(locationRepository.findAll().stream()
                    .collect(Collectors.toMap(Location::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .groups(groupRepository.findAll().stream()
                    .collect(Collectors.toMap(Group::getName, Function.identity(), (existing, replacement) -> existing, TreeMap::new)))
                .build();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                consumable.add(parseRowToTool(sheet.getRow(i), master));
            }

        } catch (IOException e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.EXCEL_PARSE_ERROR);
        }

        return consumable;
    }

    private Consumable parseRowToTool(Row row, ConsumableExcelMasterDto master) {
        String barcode = getStringCellValue(row, EExcelConsumablesPositions.BARCODE.getColumnNumber());
        Integer id = Optional.ofNullable(master.consumable.get(barcode)).map(Consumable::getId).orElse(null);

        String brandName = getStringCellValue(row, EExcelConsumablesPositions.BRAND.getColumnNumber());
        Category brand = Optional.ofNullable(master.brands.get(brandName))
            .orElseThrow(() -> new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                generateExcelErrorMessage(brandName, row.getRowNum(), EExcelConsumablesPositions.BRAND.getColumnNumber(), Messages.Error.CATEGORY_SON_NOT_FOUND
                    .formatted(CategoryParentEnum.BRANDS.getName(), brandName, CategoryParentEnum.BRANDS.getName(), master.brands.values().stream().map(Category::getName).toList().toString()))));

        String categoryName = getStringCellValue(row, EExcelConsumablesPositions.CATEGORY.getColumnNumber());
        Category category = Optional.ofNullable(master.categories.get(categoryName))
            .orElseThrow(() -> new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                generateExcelErrorMessage(categoryName, row.getRowNum(), EExcelConsumablesPositions.CATEGORY.getColumnNumber(),
                    Messages.Error.CATEGORY_SON_NOT_FOUND
                        .formatted(CategoryParentEnum.CATEGORIES.getName(), categoryName, CategoryParentEnum.CATEGORIES.getName(), master.categories.values().stream().map(Category::getName).toList().toString()))));

        String locationName = row.getCell(EExcelConsumablesPositions.LOCATION.getColumnNumber()).getStringCellValue();
        Location location = Optional.ofNullable(master.locations.get(locationName))
            .orElseThrow(() -> new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                generateExcelErrorMessage(locationName, row.getRowNum(), EExcelConsumablesPositions.LOCATION.getColumnNumber(),
                    Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.locations.values().stream().map(Location::getName).toList()))));

        String groupName = getStringCellValue(row, EExcelConsumablesPositions.GROUP.getColumnNumber());
        Group group = Optional.ofNullable(master.groups.get(groupName))
            .orElseThrow(() -> new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                generateExcelErrorMessage(groupName, row.getRowNum(), EExcelConsumablesPositions.GROUP.getColumnNumber(),
                    Messages.Error.GROUP_NOT_FOUND_EXCEL.formatted(groupName, master.groups.values().stream().map(Group::getName).toList()))));

        return Consumable.builder()
            .id(id)
            .barcode(barcode)
            .name(getStringCellValue(row, EExcelConsumablesPositions.NAME.getColumnNumber()))
            .brand(brand)
            .model(getStringCellValue(row, EExcelConsumablesPositions.MODEL.getColumnNumber()))
            .category(category)
            .name(getStringCellValue(row, EExcelConsumablesPositions.NAME.getColumnNumber()))
            .description(getStringCellValue(row, EExcelConsumablesPositions.DESCRIPTION.getColumnNumber()))
            .urlImages(getStringArrayCellValue(row, EExcelConsumablesPositions.URL_IMAGES.getColumnNumber()))
            .location(location)
            .minStock(getIntegerCellValue(row, EExcelConsumablesPositions.MIN_STOCK.getColumnNumber()))
            .price(getFloatCellValue(row, EExcelConsumablesPositions.PRICE.getColumnNumber()))
            .purchaseDate(getDateCellValue(row, EExcelConsumablesPositions.PURCHASE_DATE.getColumnNumber()))
            .stock(getIntegerCellValue(row, EExcelConsumablesPositions.STOCK.getColumnNumber()))
            .group(group)
            .minStock(getIntegerCellValue(row, EExcelConsumablesPositions.MIN_STOCK.getColumnNumber()))
            .build();
    }

    private String generateExcelErrorMessage(String value, Integer row, Integer column, String message) {
        return Messages.Error.EXCEL_VALUE_INCORRECT.formatted(value, row, column).concat("\n").concat(message);
    }

    private String getStringCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.STRING))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return cell.getStringCellValue();
    }

    private String[] getStringArrayCellValue(Row row, Integer columnNumber) {
        String cellValue = getStringCellValue(row, columnNumber);
        return cellValue.split(", ?");
    }

    private Integer getIntegerCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.NUMERIC))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.NUMERIC.toString()));

        return (int) cell.getNumericCellValue();
    }

    private LocalDate getDateCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();
        String cellValue = cell.getStringCellValue();

        if (!cellType.equals(CellType.STRING))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return stringToLocalDate(cellValue, "yyyy-MM-dd");//cell.getLocalDateTimeCellValue().toLocalDate();
    }

    private Float getFloatCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();
        if (!cellType.equals(CellType.STRING))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return convertToFloat2Decimals(cell.getStringCellValue());
    }
}

