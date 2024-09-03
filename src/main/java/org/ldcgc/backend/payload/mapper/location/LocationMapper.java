package org.ldcgc.backend.payload.mapper.location;

import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.shared.enums.ELocationType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_NULL;

@Mapper(nullValuePropertyMappingStrategy = SET_TO_NULL)
public interface LocationMapper {

    LocationMapper MAPPER = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "parentLocationId", source = "parent.id")
    @Mapping(target = "locationType", source = "level", qualifiedByName = "mapLevelToLocationType")
    LocationDto toDto(Location location);

    @Named("detailedLocation")
    @Mapping(target = "parentLocationId", source = "parent.id")
    @Mapping(target = "locationType", source = "level", qualifiedByName = "mapLevelToLocationType")
    @Mapping(target = "locations", source = ".", qualifiedByName = "mapLocationsDetailed")
    LocationDto toDtoDetailed(Location location);

    Location toMo(LocationDto toolDto);

    @Mapping(target = "id", ignore = true)
    void updateLocation(LocationDto locationDto, @MappingTarget Location location);

    @Named("mapLevelToLocationType")
    static ELocationType mapLevelToLocationType(Integer level) {
        return ObjectUtils.defaultIfNull(ELocationType.fromLevel(level), ELocationType.LOCATION);
    }

    @Named("mapLocationsDetailed")
    static List<LocationDto> mapLocationsDetailed(Location location) {
        if (ObjectUtils.isEmpty(location.getLocations())) return new ArrayList<>();

        return location.getLocations().parallelStream().map(LocationMapper.MAPPER::toDtoDetailed).collect(Collectors.toList());
    }

}
