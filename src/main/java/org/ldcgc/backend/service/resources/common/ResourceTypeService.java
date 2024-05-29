package org.ldcgc.backend.service.resources.common;

import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ResourceTypeService {

    ResponseEntity<?> getResourceTypes(String name, Boolean locked);

    ResponseEntity<?> createResourceType(ResourceTypeDto resourceTypeDto);

    ResponseEntity<?> updateResourceType(Integer resourceTypeId, ResourceTypeDto resourceTypeDto);

    ResponseEntity<?> deleteResourceType(Integer resourceId);

}
