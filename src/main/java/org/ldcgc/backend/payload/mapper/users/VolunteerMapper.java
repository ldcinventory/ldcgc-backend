package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VolunteerMapper {

    VolunteerMapper MAPPER = Mappers.getMapper(VolunteerMapper.class);

    Volunteer toEntity(VolunteerDto volunteerRequest);

    VolunteerDto toDTO(Volunteer volunteer);

}
