package org.ldcgc.backend.payload.mapper.category;

import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BrandMapper {

    BrandMapper MAPPER = Mappers.getMapper(BrandMapper.class);

    BrandDto toDto(Brand brand);
    Brand toEntity(BrandDto brandDto);

}
