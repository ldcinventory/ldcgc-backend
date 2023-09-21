package org.ldcgc.backend.payload.mapper;

import org.ldcgc.backend.db.model.users.Volunteer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import static org.ldcgc.backend.payload.dto.users.Volunteer.DTO;
import static org.ldcgc.backend.util.creation.Generator.getEncryptedPassword;

@Mapper
public interface VolunteerMapper {

    VolunteerMapper MAPPER = Mappers.getMapper(VolunteerMapper.class);

    Volunteer toEntity(DTO volunteerRequest);

    DTO toDTO(Volunteer volunteer);

}
