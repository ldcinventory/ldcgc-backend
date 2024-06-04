package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.Role;
import org.ldcgc.backend.payload.dto.users.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface RoleMapper {

    RoleMapper MAPPER = Mappers.getMapper(RoleMapper.class);

    Role toEntity(RoleDto roleDto);

    RoleDto toDto(Role role);

    void update(@MappingTarget Role role, RoleDto roleDto);

}
