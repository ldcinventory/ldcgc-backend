package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Absence;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE, nullValueCheckStrategy = ALWAYS)
public interface AbsenceMapper {

    AbsenceMapper MAPPER = Mappers.getMapper(AbsenceMapper.class);

    Absence toEntity(AbsenceDto absenceDto);

    AbsenceDto toDto(Absence absence);

    void update(@MappingTarget Absence absence, AbsenceDto absenceDto);

}
