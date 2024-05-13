package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.BrandController;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.service.resources.common.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BrandControllerImpl implements BrandController {

    private final BrandService brandService;

    public ResponseEntity<?> getResourceTypes() {
        return brandService.getResourceTypes();
    }

    public ResponseEntity<?> createResourceType(BrandDto brandDto) {
        return brandService.createResourceType(brandDto);
    }

    public ResponseEntity<?> deleteResourceType(Integer brandId) {
        return brandService.deleteResourceType(brandId);
    }
}
