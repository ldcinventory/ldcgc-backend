package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
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
    private final BrandRepository brandRepository;
    private final ResourceTypeRepository resourceTypeRepository;
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

    public ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String category, String brand, String name, String model, String description, String status, String sortField) {

        Integer statusId = StringUtils.isEmpty(status)
            ? null
            : Optional.of(status)
                .map(EStatus::getStatusByName)
                .map(EStatus::getId)
                .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.STATUS_NOT_FOUND));

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(sortField));

        Page<ToolDto> pagedTools = ObjectUtils.allNull(category, brand, name, model, description, status)
            ? toolRepository.findAll(pageable).map(ToolMapper.MAPPER::toDto)
            : toolRepository.findAllFiltered(category, brand, name, model, description, statusId, pageable).map(ToolMapper.MAPPER::toDto);

        if (pageIndex > pagedTools.getTotalPages())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

        return Constructor.buildResponseMessageObject(HttpStatus.OK,
            String.format(Messages.Info.TOOL_LISTED, pagedTools.getTotalElements()),
            PaginationDetails.fromPaging(pageable, pagedTools));
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

        return toolRepository.saveAndFlush(tool);
    }

    @Override
    public ResponseEntity<?> getAllToolsLoose(Integer pageIndex, Integer size, String filterString, String status, String sortField) {
        Integer statusId = StringUtils.isEmpty(status)
                ? null
                : Optional.of(status)
                .map(EStatus::getStatusByName)
                .map(EStatus::getId)
                .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.STATUS_NOT_FOUND));

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(sortField));

        Page<ToolDto> pagedTools = ObjectUtils.allNull(filterString, status)
                ? toolRepository.findAll(pageable).map(ToolMapper.MAPPER::toDto)
                : toolRepository.findAllFiltered(filterString, statusId, pageable).map(ToolMapper.MAPPER::toDto);

        if (pageIndex > pagedTools.getTotalPages())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

        return Constructor.buildResponseMessageObject(HttpStatus.OK,
                String.format(Messages.Info.TOOL_LISTED, pagedTools.getTotalElements()),
                PaginationDetails.fromPaging(pageable, pagedTools));
    }


    private Tool findToolOrElseThrow(Integer toolId) {
        return toolRepository.findById(toolId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.TOOL_NOT_FOUND, toolId)));
    }

    private void setLinkedEntitiesForConsumable(Tool toolEntity, ToolDto toolDto) {
        Brand brand = brandRepository.findById(toolDto.getBrand().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.BRAND_NOT_FOUND, toolDto.getBrand())));

        ResourceType consumableCategory = resourceTypeRepository.findById(toolDto.getResourceType().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CATEGORY_NOT_FOUND, toolDto.getResourceType().getId())));

        Location location = locationRepository.findById(toolDto.getLocation().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.LOCATION_NOT_FOUND, toolDto.getLocation().getId())));

        Group group = groupRepository.findById(toolDto.getGroup().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.GROUP_NOT_FOUND, toolDto.getGroup().getId())));

        toolEntity.setBrand(brand);
        toolEntity.setResourceType(consumableCategory);
        toolEntity.setLocation(location);
        toolEntity.setGroup(group);

    }
}
