package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ToolExcelMasterDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.util.common.EExcelPositions;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ToolExcelServiceImpl implements ToolExcelService {

    private final ToolRepository toolRepository;
    private final CategoryService categoryService;
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
                tools.add(parseRowToTool(sheet.getRow(i), master));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tools;
    }

    private ToolDto parseRowToTool(Row row, ToolExcelMasterDto master) {
        String barcode = getStringCellValue(row, EExcelPositions.BARCODE.getColumnNumber());
        Integer id = Optional.ofNullable(master.tools.get(barcode)).map(ToolDto::getId).orElse(null);

        String brandName = getStringCellValue(row, EExcelPositions.BRAND.getColumnNumber());
        CategoryDto brand = Optional.ofNullable(master.brands.get(brandName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(brandName, row.getRowNum(), EExcelPositions.BRAND.getColumnNumber(), Messages.Error.CATEGORY_SON_NOT_FOUND
                        .formatted(CategoryParentEnum.BRANDS.getName(), brandName, CategoryParentEnum.BRANDS.getName(), master.brands.values().stream().map(CategoryDto::getName).toList().toString()))));
        String categoryName = getStringCellValue(row, EExcelPositions.CATEGORY.getColumnNumber());
        CategoryDto category = Optional.ofNullable(master.categories.get(categoryName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(categoryName, row.getRowNum(), EExcelPositions.CATEGORY.getColumnNumber(),
                        Messages.Error.CATEGORY_SON_NOT_FOUND
                        .formatted(CategoryParentEnum.CATEGORIES.getName(), categoryName, CategoryParentEnum.CATEGORIES.getName(), master.categories.values().stream().map(CategoryDto::getName).toList().toString()))));
        EStatus status = EStatus.getStatusByName(getStringCellValue(row, EExcelPositions.STATUS.getColumnNumber()));
        String locationName = row.getCell(EExcelPositions.LOCATION.getColumnNumber()).getStringCellValue();
        LocationDto location = Optional.ofNullable(master.locations.get(locationName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(locationName, row.getRowNum(), EExcelPositions.LOCATION.getColumnNumber(),
                        Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted(locationName, master.locations.values().stream().map(LocationDto::getName).toList()))));
        ETimeUnit maintenanceTime = ETimeUnit.getTimeUnitByName(getStringCellValue(row, EExcelPositions.MAINTENANCE_TIME.getColumnNumber()));
        String groupName = getStringCellValue(row, EExcelPositions.GROUP.getColumnNumber());
        GroupDto group = Optional.ofNullable(master.groups.get(groupName))
                .orElseThrow(() -> new RequestException(generateExcelErrorMessage(groupName, row.getRowNum(), EExcelPositions.GROUP.getColumnNumber(),
                        Messages.Error.GROUP_NOT_FOUND_EXCEL.formatted(groupName, master.groups.values().stream().map(GroupDto::getName).toList()))));

        return ToolDto.builder()
                .id(id)
                .barcode(barcode)
                .name(getStringCellValue(row, EExcelPositions.NAME.getColumnNumber()))
                .brand(brand)
                .model(getStringCellValue(row, EExcelPositions.MODEL.getColumnNumber()))
                .category(category)
                .description(getStringCellValue(row, EExcelPositions.DESCRIPTION.getColumnNumber()))
                .urlImages(getStringCellValue(row, EExcelPositions.URL_IMAGES.getColumnNumber()))
                .status(status)
                .location(location)
                .maintenancePeriod(getIntegerCellValue(row, EExcelPositions.MAINTENANCE_PERIOD.getColumnNumber()))
                .maintenanceTime(maintenanceTime)
                .lastMaintenance(getDateCellValue(row, EExcelPositions.LAST_MAINTENANCE.getColumnNumber()))
                .group(group)
                .build();
    }

    private String generateExcelErrorMessage(String value, Integer row, Integer column, String message) {
        return Messages.Error.EXCEL_VALUE_INCORRECT.formatted(value, row, column).concat("\n").concat(message);
    }

    private static String getStringCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.STRING))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return cell.getStringCellValue();
    }

    private static Integer getIntegerCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.NUMERIC))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.STRING.toString()));

        return (int) cell.getNumericCellValue();
    }

    private static LocalDateTime getDateCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (!cellType.equals(CellType.NUMERIC))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, CellType.NUMERIC.toString()));

        return cell.getLocalDateTimeCellValue();
    }
}
