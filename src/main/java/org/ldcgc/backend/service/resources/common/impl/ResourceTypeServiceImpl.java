package org.ldcgc.backend.service.resources.common.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.mapper.category.ResourceTypeMapper;
import org.ldcgc.backend.service.resources.common.ResourceTypeService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceTypeServiceImpl implements ResourceTypeService {

    private final ResourceTypeRepository resourceTypeRepository;

    public ResponseEntity<?> getResourceTypes() {
        List<ResourceTypeDto> resourceTypes = resourceTypeRepository.findAll().stream()
            .map(ResourceTypeMapper.MAPPER::toDto)
            .toList();

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.RESOURCE_TYPE_FOUND, resourceTypes.size()),
            resourceTypes);

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
