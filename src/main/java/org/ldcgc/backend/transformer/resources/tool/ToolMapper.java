package org.ldcgc.backend.transformer.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.transformer.status.CustomStatusTransformer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ToolMapper {

    ToolMapper INSTANCE = Mappers.getMapper(ToolMapper.class);
    @Mapping(source = "status", target = "status", qualifiedBy = CustomStatusTransformer.class)
    ToolDto toDto(Tool tool);
    @Mapping(source = "status", target = "status", qualifiedBy = CustomStatusTransformer.class)
    Tool toMo(ToolDto toolDto);

}
