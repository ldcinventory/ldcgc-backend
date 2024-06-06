package org.ldcgc.backend.payload.mapper.group;

import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_NULL;

@Mapper(uses = LocationMapper.class,
        nullValuePropertyMappingStrategy = SET_TO_NULL)
public interface GroupMapper {

    GroupMapper MAPPER = Mappers.getMapper(GroupMapper.class);

    GroupDto toDto(Group Group);

    Group toMo(GroupDto toolDto);

}
