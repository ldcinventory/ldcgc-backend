package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.ldcgc.backend.payload.dto.excel.ToolExcelMasterDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ToolExcelServiceImpl implements ToolExcelService {

    private final ToolRepository toolRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final GroupsService groupsService;

    @Override
    public List<ToolDto> convertExcelToTools(List<ToolExcelDto> toolsExcel) {
        ToolExcelMasterDto master = getMaster(toolsExcel);

        return toolsExcel.stream()
                .map(toolExcel -> convertExcelTool(master, toolExcel))
                .toList();
    }

    private ToolExcelMasterDto getMaster(List<ToolExcelDto> toolsExcel) {
        List<ToolDto> tools = toolRepository.findByBarcodeIn(toolsExcel.stream().map(ToolExcelDto::getBarcode).toList())
                .stream()
                .map(ToolMapper.MAPPER::toDto)
                .toList();
        CategoryDto brandParent = categoryService.getCategoryParent(CategoryParentEnum.BRANDS);
        CategoryDto categoryParent = categoryService.getCategoryParent(CategoryParentEnum.CATEGORIES);
        CategoryDto maintenanceTimeParent = categoryService.getCategoryParent(CategoryParentEnum.MAINTENANCE_TIME);
        List<GroupDto> groups = groupsService.getAllGroups();
        List<LocationDto> locations = locationService.getAllLocations();

        return ToolExcelMasterDto.builder()
                .tools(tools)
                .brandParent(brandParent)
                .categoryParent(categoryParent)
                .maintenanceTimeParent(maintenanceTimeParent)
                .groups(groups)
                .locations(locations)
                .build();
    }

    //TODO: ver si este método se puede hacer con MapStruct

    private ToolDto convertExcelTool(ToolExcelMasterDto master, ToolExcelDto toolExcel) {
        Integer id = getIdByBarcode(toolExcel, master.tools);
        CategoryDto brand = categoryService.findCategorySonInParentByName(toolExcel.getBrand(), master.brandParent);
        CategoryDto category = categoryService.findCategorySonInParentByName(toolExcel.getCategory(), master.categoryParent);
        GroupDto group = groupsService.findGroupInListByName(toolExcel.getGroup(), master.groups);
        LocationDto location = locationService.findLocationInListByName(toolExcel.getLocation(), master.locations);
        CategoryDto maintenanceTime = categoryService.findCategorySonInParentByName(toolExcel.getMaintenanceTime(), master.maintenanceTimeParent);

        return ToolDto.builder()
                .id(id)
                .barcode(toolExcel.getBarcode())
                .brand(brand)
                .category(category)
                .group(group)
                .description(toolExcel.getDescription())
                .lastMaintenance(toolExcel.getLastMaintenance())
                .location(location)
                .maintenancePeriod(toolExcel.getMaintenancePeriod())
                .maintenanceTime(maintenanceTime)
                .model(toolExcel.getModel())
                .name(toolExcel.getName())
                .status(EStatus.getStatusByName(toolExcel.getStatus()))
                .urlImages(toolExcel.getUrlImages())
                .build();
    }

    private Integer getIdByBarcode(ToolExcelDto toolExcel, List<ToolDto> tools) {
        return tools.stream()
                .filter(tool -> tool.getBarcode().equals(toolExcel.getBarcode()))
                .map(ToolDto::getId)
                .findFirst()
                .orElse(null);
    }
}

