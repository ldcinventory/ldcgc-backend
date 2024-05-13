package org.ldcgc.backend.payload.mapper.location;

import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface LocationMapper {

    LocationMapper MAPPER = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "parentLocationId", source = "parent.id")
    LocationDto toDto(Location location);

    @Named("detailedLocation")
    @Mapping(target = "parentLocationId", source = "parent.id")
    LocationDto toDtoDetailed(Location location);

    Location toMo(LocationDto toolDto);

    @Mapping(target = "id", ignore = true)
    void updateLocation(LocationDto locationDto, @MappingTarget Location location);

}
