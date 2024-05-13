package org.ldcgc.backend.payload.mapper.category;

import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResourceTypeMapper {

    ResourceTypeMapper MAPPER = Mappers.getMapper(ResourceTypeMapper.class);

    ResourceTypeDto toDto(ResourceType brand);
    ResourceType toEntity(ResourceTypeDto brandDto);

}
