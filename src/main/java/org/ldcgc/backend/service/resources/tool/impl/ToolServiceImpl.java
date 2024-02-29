package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.ToolExcelService;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.util.common.EStatus;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;
    private final ToolExcelService toolExcelService;

    public ResponseEntity<?> getTool(Integer toolId) {
        Tool tool = findToolOrElseThrow(toolId);
        return Constructor.buildResponseObject(HttpStatus.OK, ToolMapper.MAPPER.toDto(tool));
    }

    public ResponseEntity<?> createTool(ToolDto toolDto) {
        if(Objects.nonNull(toolDto.getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOOL_ID_SHOULDNT_BE_PRESENT);

        Optional<Tool> repeatedTool = toolRepository.findFirstByBarcode(toolDto.getBarcode());
        if(repeatedTool.isPresent())
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.TOOL_BARCODE_ALREADY_EXISTS, toolDto.getBarcode()));

        Tool entityTool = ToolMapper.MAPPER.toMo(toolDto);
        setLinkedEntitiesForConsumable(entityTool, toolDto);
        entityTool = toolRepository.saveAndFlush(entityTool);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.TOOL_CREATED, ToolMapper.MAPPER.toDto(entityTool));
    }

    public ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto) {
        Tool toolToUpdate = findToolOrElseThrow(toolId);

        Optional<Tool> toolByBarcode = toolRepository.findFirstByBarcode(toolDto.getBarcode());
        if(toolByBarcode.isPresent() && !toolByBarcode.get().getId().equals(toolId))
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.TOOL_BARCODE_ALREADY_EXISTS, toolDto.getBarcode()));

        ToolMapper.MAPPER.update(toolDto, toolToUpdate);
        setLinkedEntitiesForConsumable(toolToUpdate, toolDto);
        toolToUpdate = toolRepository.saveAndFlush(toolToUpdate);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.TOOL_UPDATED, ToolMapper.MAPPER.toDto(toolToUpdate));
    }

    public ResponseEntity<?> deleteTool(Integer toolId) {
        Tool tool = findToolOrElseThrow(toolId);
        toolRepository.delete(tool);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.TOOL_DELETED);
    }

    public ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String sortField, String brand, String model, String description, String status) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(sortField));

        Integer statusId = null;

        if(Objects.nonNull(status))
            statusId = EStatus.getStatusByName(status).getId();

        Page<ToolDto> page = toolRepository.findAllFiltered(brand, model, description, statusId, pageable)
                .map(ToolMapper.MAPPER::toDto);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.TOOL_LISTED, page.getTotalElements()),
            page);
    }

    public ResponseEntity<?> uploadToolsExcel(MultipartFile file) {
        List<ToolDto> toolsToSave = toolExcelService.excelToTools(file);

        toolRepository.saveAll(toolsToSave.stream().map(ToolMapper.MAPPER::toMo).toList());

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.TOOL_UPLOADED, toolsToSave.size()),
            toolsToSave);
    }

    public Tool updateToolStatus(Tool tool, EStatus status){
        tool.setStatus(status);

        return toolRepository.save(tool);
    }


    private Tool findToolOrElseThrow(Integer toolId) {
        return toolRepository.findById(toolId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TOOL_NOT_FOUND, toolId)));
    }

    private void setLinkedEntitiesForConsumable(Tool toolEntity, ToolDto toolDto) {
        Category brand = categoryRepository.findById(toolDto.getBrand().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.BRAND_NOT_FOUND, toolDto.getBrand())));

        Category consumableCategory = categoryRepository.findById(toolDto.getCategory().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CATEGORY_NOT_FOUND, toolDto.getCategory().getId())));

        Location location = locationRepository.findById(toolDto.getLocation().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.LOCATION_NOT_FOUND, toolDto.getLocation().getId())));

        Group group = groupRepository.findById(toolDto.getGroup().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.GROUP_NOT_FOUND, toolDto.getGroup().getId())));

        toolEntity.setBrand(brand);
        toolEntity.setCategory(consumableCategory);
        toolEntity.setLocation(location);
        toolEntity.setGroup(group);

    }
}
