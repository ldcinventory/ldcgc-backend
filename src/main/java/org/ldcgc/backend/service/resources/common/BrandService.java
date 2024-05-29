package org.ldcgc.backend.service.resources.common;

import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BrandService {

    ResponseEntity<?> getBrands(String name, Boolean locked);

    ResponseEntity<?> createBrand(BrandDto brandDto);

    ResponseEntity<?> updateBrand(Integer brandId, BrandDto brandDto);

    ResponseEntity<?> deleteBrand(Integer brandId);

    void lockBrand(Brand brand);

    boolean brandIsLocked(Brand brand);

}
