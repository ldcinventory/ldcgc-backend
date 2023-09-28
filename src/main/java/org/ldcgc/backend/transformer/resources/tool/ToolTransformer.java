package org.ldcgc.backend.transformer.resources.tool;


import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;

public interface ToolTransformer {

    ToolDto toDto(Tool tool);

    Tool toMo(ToolDto toolDto);

}
