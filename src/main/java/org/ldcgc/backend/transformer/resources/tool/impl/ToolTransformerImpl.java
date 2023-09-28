package org.ldcgc.backend.transformer.resources.tool.impl;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.transformer.resources.tool.ToolMapper;
import org.ldcgc.backend.transformer.resources.tool.ToolTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToolTransformerImpl implements ToolTransformer {

    @Autowired
    private ToolMapper mapper;

    @Override
    public ToolDto toDto(Tool tool) {
        return mapper.toDto(tool);
    }

    @Override
    public Tool toMo(ToolDto toolDto) {
        return mapper.toMo(toolDto);
    }
}
