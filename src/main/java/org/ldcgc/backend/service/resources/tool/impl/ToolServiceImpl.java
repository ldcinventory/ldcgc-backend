package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.StatusRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.transformer.resources.tool.ToolMapper;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.STATUS_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;
    private final StatusRepository statusRepository;

    public ResponseEntity<?> createTool(ToolDto tool) {

        Tool entityTool = ToolMapper.INSTANCE.toMo(tool);

        Status status = statusRepository.findByName(tool.getStatus()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(STATUS_NOT_FOUND)));

        entityTool.setStatus(status);
        entityTool = toolRepository.save(entityTool);

        return Constructor.buildResponseObject(HttpStatus.OK, entityTool);
    }
}
