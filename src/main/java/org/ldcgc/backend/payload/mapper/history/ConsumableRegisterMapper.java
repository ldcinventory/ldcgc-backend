package org.ldcgc.backend.payload.mapper.history;

import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ConsumableRegisterMapper {

    ConsumableRegisterMapper MAPPER = Mappers.getMapper(ConsumableRegisterMapper.class);

    ConsumableRegisterDto toDto(ConsumableRegister tool);
    ConsumableRegister toEntity(ConsumableRegisterDto toolDto);
    void update(ConsumableRegisterDto from, @MappingTarget ConsumableRegister to);

}
