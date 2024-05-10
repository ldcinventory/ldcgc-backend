package org.ldcgc.backend.service.resources.common.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.other.NonPaged;
import org.ldcgc.backend.payload.mapper.category.BrandMapper;
import org.ldcgc.backend.service.resources.common.BrandService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public ResponseEntity<?> getResourceTypes() {
        List<BrandDto> brands = brandRepository.findAll().stream()
            .map(BrandMapper.MAPPER::toDto)
            .toList();

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.BRAND_FOUND, brands.size()),
            NonPaged.of(brands));

    }

    public ResponseEntity<?> createResourceType(BrandDto brandDto) {
        if(brandRepository.existsByName(brandDto.getName()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.BRAND_EXISTS);

        Brand brand = BrandMapper.MAPPER.toEntity(brandDto);
        brand = brandRepository.save(brand);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.BRAND_CREATED,
            brand);

    }

    public ResponseEntity<?> deleteResourceType(Integer brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.BRAND_NOT_FOUND, brandId)));

        if(brand.getLocked())
            throw new RequestException(HttpStatus.FORBIDDEN, String.format(Messages.Error.BRAND_LOCKED, brandId));

        brandRepository.delete(brand);

        return Constructor.buildResponseMessage(
            HttpStatus.OK,
            String.format(Messages.Info.BRAND_DELETED, brandId));

    }

}
