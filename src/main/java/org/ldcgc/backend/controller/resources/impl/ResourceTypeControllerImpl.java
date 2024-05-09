package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ResourceTypeController;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.service.resources.common.ResourceTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResourceTypeControllerImpl implements ResourceTypeController {

    private final ResourceTypeService resourceTypeService;

    public ResponseEntity<?> getResourceTypes() {
        return resourceTypeService.getResourceTypes();
    }

    public ResponseEntity<?> createResourceType(ResourceTypeDto resourceTypeDto) {
        return resourceTypeService.createResourceType(resourceTypeDto);
    }

    public ResponseEntity<?> deleteResourceType(Integer resourceId) {
        return resourceTypeService.deleteResourceType(resourceId);
    }
}
