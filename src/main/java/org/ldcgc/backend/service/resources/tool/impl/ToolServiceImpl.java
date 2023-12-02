package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.common.ExcelUtils;
import org.ldcgc.backend.util.creation.Constructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.*;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final GroupsService groupsService;

    public ResponseEntity<?> getTool(Integer toolId) {
        Tool tool = toolRepository.findById(toolId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(TOOL_NOT_FOUND), toolId)));
        return Constructor.buildResponseObject(HttpStatus.OK, ToolMapper.MAPPER.toDto(tool));
    }

    public ResponseEntity<?> createTool(ToolDto tool) {

        Tool entityTool = toolRepository.save(ToolMapper.MAPPER.toMo(tool));

        ToolDto toolDto = ToolMapper.MAPPER.toDto(entityTool);

        return Constructor.buildResponseObject(HttpStatus.OK, toolDto);
    }

    @Override
    public ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto) {
        toolRepository.save(ToolMapper.MAPPER.toMo(toolDto));
        return Constructor.buildResponseObject(HttpStatus.OK, toolDto);
    }

    @Override
    public ResponseEntity<?> deleteTool(Integer toolId) {
        Tool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(TOOL_NOT_FOUND), toolId)));
        toolRepository.delete(tool);

        return Constructor.buildResponseObject(HttpStatus.OK, ToolMapper.MAPPER.toDto(tool));
    }

    @Override
    public ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String filterString) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(filterString));
        Page<ToolDto> page = toolRepository.findAll(pageable).map(ToolMapper.MAPPER::toDto);

        return Constructor.buildResponseObject(HttpStatus.OK, page);
    }

    @Override
    public ResponseEntity<?> uploadToolsExcel(MultipartFile file) {
        List<ToolExcelDto> toolsExcel = ExcelUtils.excelToTools(file);

        List<ToolDto> toolsToSave = convertExcelTools(toolsExcel);

        toolRepository.saveAll(toolsToSave.stream().map(ToolMapper.MAPPER::toMo).toList());

        return Constructor.buildResponseObject(HttpStatus.OK, toolsToSave);
    }

    private List<ToolDto> convertExcelTools(List<ToolExcelDto> toolsExcel) {
        List<ToolDto> tools = toolRepository.findByBarcodeIn(toolsExcel.stream().map(ToolExcelDto::getBarcode).toList())
                .stream()
                .map(ToolMapper.MAPPER::toDto)
                .toList();
        CategoryDto brandParent = categoryService.getCategoryParent(CategoryParentEnum.BRANDS);
        CategoryDto categoryParent = categoryService.getCategoryParent(CategoryParentEnum.CATEGORIES);
        List<GroupDto> groups = groupsService.getAllGroups();
        List<LocationDto> locations = locationService.getAllLocations();
        CategoryDto maintenanceTimeParent = categoryService.getCategoryParent(CategoryParentEnum.MAINTENANCE_TIME);

        return toolsExcel.stream()
                .map(toolExcel -> ToolDto.builder()
                        .id(getIdByBarcode(toolExcel, tools))
                        .barcode(toolExcel.getBarcode())
                        .brand(categoryService.findCategorySonInParentByName(toolExcel.getBrand(), brandParent))
                        .category(categoryService.findCategorySonInParentByName(toolExcel.getCategory(), categoryParent))
                        .group(groupsService.findGroupInListByName(toolExcel.getGroup(), groups))
                        .description(toolExcel.getDescription())
                        .lastMaintenance(toolExcel.getLastMaintenance())
                        .location(locationService.findLocationInListByName(toolExcel.getLocation(), locations))
                        .maintenancePeriod(toolExcel.getMaintenancePeriod())
                        .maintenanceTime(categoryService.findCategorySonInParentByName(toolExcel.getMaintenanceTime(), maintenanceTimeParent))
                        .model(toolExcel.getModel())
                        .name(toolExcel.getName())
                        .status(EStatus.getStatusByName(toolExcel.getStatus()))
                        .urlImages(toolExcel.getUrlImages())
                        .build())
                .toList();
    }


    @Nullable
    private Integer getIdByBarcode(ToolExcelDto toolExcel, List<ToolDto> tools) {
        return tools.stream()
                .filter(tool -> tool.getBarcode().equals(toolExcel.getBarcode()))
                .map(ToolDto::getId)
                .findFirst()
                .orElse(null);
    }
}
