package org.ldcgc.backend.payload.mapper;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.ldcgc.backend.payload.dto.users.Volunteer.DTO;

@Mapper
public interface VolunteerMapper {

    VolunteerMapper MAPPER = Mappers.getMapper(VolunteerMapper.class);

    Volunteer toEntity(DTO volunteerRequest);

    DTO toDTO(Volunteer volunteer);

}
