package org.ldcgc.backend.payload.mapper.category;

import org.ldcgc.backend.db.model.category.Responsibility;
import org.ldcgc.backend.payload.dto.category.ResponsibilityDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResponsibilityMapper {

    ResponsibilityMapper MAPPER = Mappers.getMapper(ResponsibilityMapper.class);

    ResponsibilityDto toDto(Responsibility responsibility);
    Responsibility toEntity(ResponsibilityDto responsibilityDto);

}
