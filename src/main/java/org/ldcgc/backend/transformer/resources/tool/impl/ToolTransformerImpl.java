package org.ldcgc.backend.transformer.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.transformer.resources.tool.ToolMapper;
import org.ldcgc.backend.transformer.resources.tool.ToolTransformer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToolTransformerImpl implements ToolTransformer {

    public ToolDto toDto(Tool tool) {
        return ToolMapper.INSTANCE.toDto(tool);
    }

    public Tool toMo(ToolDto toolDto) {
        return ToolMapper.INSTANCE.toMo(toolDto);
    }

}
