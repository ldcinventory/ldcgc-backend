package org.ldcgc.backend.payload.mapper.resources.consumable;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.common.MapperMethods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(uses = MapperMethods.class,
        nullValuePropertyMappingStrategy = IGNORE)
public interface ConsumableMapper {

    ConsumableMapper MAPPER = Mappers.getMapper(ConsumableMapper.class);

    @Mapping(target = "brand.locked", ignore = true)
    @Mapping(target = "resourceType.locked", ignore = true)
    @Mapping(target = "location.locations", ignore = true)
    @Mapping(target = "location.parentLocationId", source = "location.parent.id")
    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "mapUrlImages")
    ConsumableDto toDto(Consumable consumable);

    static ConsumableDto cleanProps(ConsumableDto consumableDto) {
        consumableDto.getGroup().setLocation(null);
        return consumableDto;
    }

    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "mapBarcode")
    Consumable toMo(ConsumableDto consumableDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "resourceType", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "group", ignore = true)
    void update(ConsumableDto from, @MappingTarget Consumable to);

}
