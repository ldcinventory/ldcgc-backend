package org.ldcgc.backend.transformer.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.transformer.resources.status.StatusToEStatus;
import org.ldcgc.backend.transformer.resources.status.impl.StatusTransformerImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = { StatusTransformerImpl.class })
public interface ToolMapper {

    ToolMapper INSTANCE = Mappers.getMapper(ToolMapper.class);

    @Mapping(target = "status", qualifiedBy = { StatusToEStatus.class })
    ToolDto toDto(Tool tool);

    @Mapping(target = "status", ignore = true)
    Tool toMo(ToolDto toolDto);

}
