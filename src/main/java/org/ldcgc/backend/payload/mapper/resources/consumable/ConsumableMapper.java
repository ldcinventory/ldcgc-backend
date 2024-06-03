package org.ldcgc.backend.payload.mapper.resources.consumable;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.util.constants.Google;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConsumableMapper {

    ConsumableMapper MAPPER = Mappers.getMapper(ConsumableMapper.class);

    @Mapping(target = "location.locations", ignore = true)
    @Mapping(target = "group.location.locations", ignore = true)
    @Mapping(target = "resourceType.locked", ignore = true)
    @Mapping(target = "brand.locked", ignore = true)
    @Mapping(target = "urlImages", source = "urlImages", qualifiedByName = "mapConsumableUrlImagesToDto")
    ConsumableDto toDto(Consumable consumable);

    static ConsumableDto cleanProps(ConsumableDto consumableDto) {
        consumableDto.getLocation().setLocations(null);
        consumableDto.getLocation().setStoresResources(null);
        consumableDto.getGroup().setLocation(null);
        consumableDto.getBrand().setLocked(null);
        consumableDto.getResourceType().setLocked(null);
        return consumableDto;
    }

    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "mapConsumableBarcode")
    Consumable toMo(ConsumableDto consumableDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "resourceType", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "group", ignore = true)
    void update(ConsumableDto from, @MappingTarget Consumable to);

    @Named("mapConsumableUrlImagesToDto")
    static String[] mapConsumableUrlImagesToDto(String[] urlImages){
        if(urlImages == null) return null;

        return Arrays.stream(urlImages)
            .map(url -> String.format(Google.DRIVE_IMAGES_URL, url))
            .toArray(String[]::new);
    }

    @Named("mapConsumableBarcode")
    static String mapToolBarcode(String barcode) {
        return StringUtils.defaultIfBlank(barcode,
            "#" + RandomStringUtils.randomAlphanumeric(8).toUpperCase());
    }

}
