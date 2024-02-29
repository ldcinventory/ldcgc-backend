package org.ldcgc.backend.payload.mapper.history.tool;


import org.ldcgc.backend.db.model.history.ToolRegister;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;


@Mapper(uses = ToolMapper.class, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ToolRegisterMapper {
    ToolRegisterMapper MAPPER = Mappers.getMapper(ToolRegisterMapper.class);

    @Mapping(target = "volunteer.absences", ignore = true)
    @Mapping(target = "volunteer.availability", ignore = true)
    ToolRegisterDto toDto(ToolRegister mo);

    ToolRegister toMo(ToolRegisterDto dto);

    void update(ToolRegisterDto from, @MappingTarget ToolRegister to);
}
