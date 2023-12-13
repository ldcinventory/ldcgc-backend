package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Availability;
import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE, nullValueCheckStrategy = ALWAYS)
public interface AvailabilityMapper {

    AvailabilityMapper MAPPER = Mappers.getMapper(AvailabilityMapper.class);

    Availability toEntity(AvailabilityDto availabilityDto);

    AvailabilityDto toDto(Availability availability);

    void update(@MappingTarget Availability availability, AvailabilityDto availabilityDto);

}
