package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE, nullValueCheckStrategy = ALWAYS,
    uses = { AbsenceMapper.class })
public interface VolunteerMapper {

    VolunteerMapper MAPPER = Mappers.getMapper(VolunteerMapper.class);

    Volunteer toEntity(VolunteerDto volunteerRequest);

    VolunteerDto toDto(Volunteer volunteer);

    void update(@MappingTarget Volunteer volunteer, VolunteerDto volunteerDto);

}
