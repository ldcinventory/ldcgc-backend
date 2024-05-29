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

    public ResponseEntity<?> getBrands(String name, Boolean locked) {
        return brandService.getBrands(name, locked);
    }

    public ResponseEntity<?> createBrand(BrandDto brandDto) {
        return brandService.createBrand(brandDto);
    }

    public ResponseEntity<?> updateBrand(Integer brandId, BrandDto brandDto) {
        return brandService.updateBrand(brandId, brandDto);
    }

    public ResponseEntity<?> deleteBrand(Integer brandId) {
        return brandService.deleteBrand(brandId);
    }
}
