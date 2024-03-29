package org.ldcgc.backend.payload.mapper.resources.consumable;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.util.constants.Google;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

@Mapper(uses = LocationMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsumableMapper {

    ConsumableMapper MAPPER = Mappers.getMapper(ConsumableMapper.class);

    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "location.locations", ignore = true)
    @Mapping(target = "location.parent.locations", ignore = true)
    @Mapping(target = "category.locked", ignore = true)
    @Mapping(target = "category.categories", ignore = true)
    @Mapping(target = "category.parent.locked", ignore = true)
    @Mapping(target = "brand.locked", ignore = true)
    @Mapping(target = "brand.categories", ignore = true)
    @Mapping(target = "brand.parent.locked", ignore = true)
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "mapUrlImagesToDto")
    ConsumableDto toDto(Consumable consumable);

    @Named("mapUrlImagesToDto")
    static String[] mapUrlImagesToDto(String[] urlImages){
        if(urlImages == null) return null;

        return Arrays.stream(urlImages)
            .map(url -> String.format(Google.DRIVE_IMAGES_URL, url))
            .toArray(String[]::new);
    }

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "group", ignore = true)
    Consumable toMo(ConsumableDto consumableDto);

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "group", ignore = true)
    void update(ConsumableDto from, @MappingTarget Consumable to);

}
