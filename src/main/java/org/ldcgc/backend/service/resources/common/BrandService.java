package org.ldcgc.backend.service.resources.common;

import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface BrandService {

    ResponseEntity<?> getResourceTypes();

    ResponseEntity<?> createResourceType(BrandDto brandDto);

    ResponseEntity<?> deleteResourceType(Integer brandId);

}
