package org.ldcgc.backend.payload.mapper.resources.consumable;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.category.CategoryMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { LocationMapper.class, CategoryMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsumableMapper {

    ConsumableMapper MAPPER = Mappers.getMapper(ConsumableMapper.class);

    @Mapping(target = "group.location.locations", ignore = true)
    ConsumableDto toDto(Consumable consumable);

    Consumable toMo(ConsumableDto consumableDto);

    void update(ConsumableDto from, @MappingTarget Consumable to);

}
