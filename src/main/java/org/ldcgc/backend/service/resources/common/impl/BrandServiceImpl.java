package org.ldcgc.backend.service.resources.common.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
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
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ToolRepository toolRepository;
    private final ConsumableRepository consumableRepository;

    public ResponseEntity<?> getBrands(String name, Boolean locked) {
        List<Brand> brands = StringUtils.isBlank(name) && locked == null
            ? brandRepository.findAll()
            : brandRepository.findAllFiltered(
            Optional.ofNullable(name).map(String::trim).orElse(null), locked);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.BRAND_FOUND, brands.size()),
            NonPaged.of(brands.stream().map(BrandMapper.MAPPER::toDto).toList()));
    }

    public ResponseEntity<?> createBrand(BrandDto brandDto) {
        if(brandRepository.existsByName(brandDto.getName()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.BRAND_EXISTS);

        Brand brand = BrandMapper.MAPPER.toEntity(brandDto);
        brand = brandRepository.save(brand);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.BRAND_CREATED,
            brand);
    }

    public ResponseEntity<?> updateBrand(Integer brandId, BrandDto brandDto) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.RESOURCE_TYPE_NOT_FOUND, brandId)));

        if(brand.getName().equals(brandDto.getName())
            && brand.getLocked().equals(brandDto.getLocked()))
            return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.NO_CHANGES_PROCESSED);

        Brand existingBrand = brandRepository.findByName(brandDto.getName()).orElse(null);

        // check duplicates
        if(existingBrand != null
            && !Objects.equals(existingBrand.getId(), brandId)
            && existingBrand.getName().equals(brandDto.getName()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.RESOURCE_TYPE_EXISTS);

        brand.setName(brandDto.getName());
        brand.setLocked(brandDto.getLocked());

        brand = brandRepository.save(brand);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            Messages.Info.RESOURCE_TYPE_UPDATED,
            BrandMapper.MAPPER.toDto(brand));
    }

    public ResponseEntity<?> deleteBrand(Integer brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.BRAND_NOT_FOUND, brandId)));

        if(brand.getLocked())
            throw new RequestException(HttpStatus.FORBIDDEN, String.format(Messages.Error.BRAND_LOCKED, brandId));

        brandRepository.delete(brand);

        return Constructor.buildResponseMessage(
            HttpStatus.OK,
            String.format(Messages.Info.BRAND_DELETED, brandId));
    }

    public void lockBrand(Brand brand) {
        // set brand locked to protect from inconsistency in DB
        if(!brandIsLocked(brand)) {
            brand.setLocked(true);
            brandRepository.saveAndFlush(brand);
        }
    }

    public boolean brandIsLocked(Brand brand) {
        return toolRepository.brandUsed(brand.getId()) || consumableRepository.brandUsed(brand.getId());
    }

}
