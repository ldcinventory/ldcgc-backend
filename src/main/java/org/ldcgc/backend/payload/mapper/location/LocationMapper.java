package org.ldcgc.backend.payload.mapper.location;

import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface LocationMapper {

    LocationMapper MAPPER = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "parent.locations", ignore = true)
    @Mapping(target = "locations", qualifiedByName = "mapNestedLocationsParentAsNull")
    LocationDto toDto(Location location);

    Location toMo(LocationDto toolDto);

    @Named("mapNestedLocationsParentAsNull")
    static List<LocationDto> mapNestedLocationsParentAsNull(List<Location> locations) {
        return locations.stream().map(location -> {
            location.setParent(null);
            return LocationMapper.MAPPER.toDto(location);
        }).toList();
    }
}
