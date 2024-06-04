package org.ldcgc.backend.payload.mapper.users;

import org.ldcgc.backend.db.model.users.RoleRegister;
import org.ldcgc.backend.payload.dto.users.RoleRegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(nullValuePropertyMappingStrategy = IGNORE)
public interface RoleRegisterMapper {

    RoleRegisterMapper MAPPER = Mappers.getMapper(RoleRegisterMapper.class);

    RoleRegister toEntity(RoleRegisterDto roleRegisterDto);

    RoleRegisterDto toDto(RoleRegister roleRegister);

    void update(@MappingTarget RoleRegister roleRegister, RoleRegisterDto roleRegisterDto);

}
