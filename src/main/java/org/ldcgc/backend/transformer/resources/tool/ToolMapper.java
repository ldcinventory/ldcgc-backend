package org.ldcgc.backend.transformer.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.transformer.status.CustomStatusTransformer;
import org.ldcgc.backend.transformer.status.EStatusToStatus;
import org.ldcgc.backend.transformer.status.StatusToEStatus;
import org.ldcgc.backend.transformer.status.impl.CustomStatusTransformerImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = CustomStatusTransformerImpl.class)
public interface ToolMapper {

    ToolMapper INSTANCE = Mappers.getMapper(ToolMapper.class);

    @Mapping(target = "status", qualifiedBy = { CustomStatusTransformer.class, StatusToEStatus.class })
    ToolDto toDto(Tool tool);

    @Mapping(target = "status", qualifiedBy = { CustomStatusTransformer.class, EStatusToStatus.class })
    Tool toMo(ToolDto toolDto);

}
