package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.db.model.resources.Tool;
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
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.ETimeUnit;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                            .collect(Collectors.toMap(ToolDto::getBarcode, Function.identity(), (existing, replacement) -> existing)))
                    .brands(categoryService.getCategoryParent(CategoryParentEnum.BRANDS).getCategories()
                            .stream().collect(Collectors.toMap(CategoryDto::getName, Function.identity(), (existing, replacement) -> existing)))
                    .categories(categoryService.getCategoryParent(CategoryParentEnum.CATEGORIES).getCategories()
                            .stream().collect(Collectors.toMap(CategoryDto::getName, Function.identity(), (existing, replacement) -> existing)))
                    .locations(locationService.getAllLocations()
                            .stream().collect(Collectors.toMap(LocationDto::getName, Function.identity(), (existing, replacement) -> existing)))
                    .groups(groupsService.getAllGroups()
                            .stream().collect(Collectors.toMap(GroupDto::getName, Function.identity(), (existing, replacement) -> existing)))
                    .build();
            sheet.forEach(row -> {
                if (row.getRowNum() != 0)
                    tools.add(parseRowToTool(row, master));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tools;
    }

    private ToolDto parseRowToTool(Row row, ToolExcelMasterDto master) {
        String barcode = row.getCell(0).getStringCellValue();
        Integer id = master.tools.get(barcode).getId();

        String brandName = row.getCell(2).getStringCellValue();
        CategoryDto brand = Optional.of(master.brands.get(brandName))
                .orElseThrow(() -> new RequestException( Messages.Error.CATEGORY_SON_NOT_FOUND
                        .formatted(CategoryParentEnum.BRANDS.getName(), brandName, CategoryParentEnum.BRANDS.getName(), master.brands.values().stream().map(CategoryDto::getName).toList().toString())));
        String categoryName = row.getCell(4).getStringCellValue();
        CategoryDto category = Optional.of(master.categories.get(categoryName))
                .orElseThrow(() -> new RequestException( Messages.Error.CATEGORY_SON_NOT_FOUND
                        .formatted(CategoryParentEnum.CATEGORIES.getName(), categoryName, CategoryParentEnum.CATEGORIES.getName(), master.categories.values().stream().map(CategoryDto::getName).toList().toString())));
        EStatus status = EStatus.getStatusByName(row.getCell(7).getStringCellValue());
        String locationName = row.getCell(8).getStringCellValue();
        LocationDto location = Optional.of(master.locations.get(locationName))
                .orElseThrow(() -> new RequestException(String.format(Messages.Error.LOCATION_NOT_FOUND_EXCEL, locationName, master.locations.values().stream().map(LocationDto::getName).toList())));
        ETimeUnit maintenanceTime = ETimeUnit.getTimeUnitByName(row.getCell(10).getStringCellValue());
        String groupName = row.getCell(12).getStringCellValue();
        GroupDto group = Optional.of(master.groups.get(groupName))
                .orElseThrow(() -> new RequestException(String.format(Messages.Error.GROUP_NOT_FOUND_EXCEL, groupName, master.groups.values().stream().map(GroupDto::getName).toList())));

        return ToolDto.builder()
                .id(id)
                .barcode(barcode)
                .name(row.getCell(1).getStringCellValue())
                .brand(brand)
                .model(row.getCell(3).getStringCellValue())
                .category(category)
                .description(row.getCell(5).getStringCellValue())
                .urlImages(row.getCell(6).getStringCellValue())
                .status(status)
                .location(location)
                .maintenancePeriod((int) row.getCell(9).getNumericCellValue())
                .maintenanceTime(maintenanceTime)
                .lastMaintenance(row.getCell(11).getLocalDateTimeCellValue())
                .group(group)
                .build();
    }
}
