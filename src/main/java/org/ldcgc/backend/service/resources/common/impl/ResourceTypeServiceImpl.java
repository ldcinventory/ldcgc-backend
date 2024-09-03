package org.ldcgc.backend.service.resources.common.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.app.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.other.NonPaged;
import org.ldcgc.backend.payload.mapper.category.ResourceTypeMapper;
import org.ldcgc.backend.service.resources.common.ResourceTypeService;
import org.ldcgc.backend.shared.constants.Messages;
import org.ldcgc.backend.shared.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResourceTypeServiceImpl implements ResourceTypeService {

    private final ResourceTypeRepository resourceTypeRepository;

    public ResponseEntity<?> getResourceTypes(String name, Boolean locked) {
        List<ResourceType> resourceTypes = StringUtils.isBlank(name) && locked == null
            ? resourceTypeRepository.findAll()
            : resourceTypeRepository.findAllFiltered(
                Optional.ofNullable(name).map(String::trim).orElse(null), locked);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.RESOURCE_TYPE_FOUND, resourceTypes.size()),
            NonPaged.of(resourceTypes.stream().map(ResourceTypeMapper.MAPPER::toDto).toList()));
    }

    public ResponseEntity<?> createResourceType(ResourceTypeDto resourceTypeDto) {
        if(resourceTypeRepository.existsByName(resourceTypeDto.getName()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.RESOURCE_TYPE_EXISTS);

        ResourceType resourceType = ResourceTypeMapper.MAPPER.toEntity(resourceTypeDto);
        resourceType = resourceTypeRepository.save(resourceType);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.RESOURCE_TYPE_CREATED,
            resourceType);

    }

    public ResponseEntity<?> updateResourceType(Integer resourceTypeId, ResourceTypeDto resourceTypeDto) {
        ResourceType resourceType = resourceTypeRepository.findById(resourceTypeId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.RESOURCE_TYPE_NOT_FOUND, resourceTypeId)));

        if(resourceType.getName().equals(resourceTypeDto.getName())
            && resourceType.getLocked().equals(resourceTypeDto.getLocked()))
            return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.NO_CHANGES_PROCESSED);

        ResourceType existingResourceType = resourceTypeRepository.findByName(resourceTypeDto.getName()).orElse(null);

        // check duplicates
        if(existingResourceType != null
            && !Objects.equals(existingResourceType.getId(), resourceTypeId)
            && existingResourceType.getName().equals(resourceTypeDto.getName()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.RESOURCE_TYPE_EXISTS);

        resourceType.setName(resourceTypeDto.getName());
        resourceType.setLocked(resourceTypeDto.getLocked());

        resourceType = resourceTypeRepository.save(resourceType);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.RESOURCE_TYPE_UPDATED,
            ResourceTypeMapper.MAPPER.toDto(resourceType));
    }

    public ResponseEntity<?> deleteResourceType(Integer resourceId) {
        ResourceType resourceType = resourceTypeRepository.findById(resourceId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.RESOURCE_TYPE_NOT_FOUND, resourceId)));

        if(resourceType.getLocked())
            throw new RequestException(HttpStatus.FORBIDDEN, String.format(Messages.Error.RESOURCE_TYPE_LOCKED, resourceId));

        resourceTypeRepository.delete(resourceType);

        return Constructor.buildResponseMessage(
            HttpStatus.OK,
            String.format(Messages.Info.RESOURCE_TYPE_DELETED, resourceId));

    }

}
