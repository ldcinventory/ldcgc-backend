package org.ldcgc.backend.payload.mapper.group;

import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface GroupMapper {

    GroupMapper MAPPER = Mappers.getMapper(GroupMapper.class);

    GroupDto toDto(Group Group);

    Group toMo(GroupDto toolDto);

}
