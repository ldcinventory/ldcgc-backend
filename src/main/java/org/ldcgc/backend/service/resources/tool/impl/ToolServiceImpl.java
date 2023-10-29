package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.TOOL_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;

    public ResponseEntity<?> getTool(Integer toolId) {
        Tool tool = toolRepository.findById(toolId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(TOOL_NOT_FOUND), toolId)));
        return Constructor.buildResponseObject(HttpStatus.OK, ToolMapper.MAPPER.toDto(tool));
    }

    public ResponseEntity<?> createTool(ToolDto tool) {

        Tool entityTool = toolRepository.save(ToolMapper.MAPPER.toMo(tool));

        ToolDto toolDto = ToolMapper.MAPPER.toDto(entityTool);

        return Constructor.buildResponseObject(HttpStatus.OK, toolDto);
    }

    @Override
    public ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto) {
        //Optional<Tool> tool = toolRepository.findById(toolId);
        toolRepository.save(ToolMapper.MAPPER.toMo(toolDto));
        return Constructor.buildResponseObject(HttpStatus.OK, toolDto);
    }

    @Override
    public ResponseEntity<?> deleteTool(Integer toolId) {
        Tool tool = toolRepository.findById(toolId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(TOOL_NOT_FOUND), toolId)));
        toolRepository.delete(tool);

        return Constructor.buildResponseObject(HttpStatus.OK, ToolMapper.MAPPER.toDto(tool));
    }

}
