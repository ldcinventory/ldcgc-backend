package org.ldcgc.backend.service.tools.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.tools.ToolService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToolServiceImpl implements ToolService {

    @Override
    public ResponseEntity<?> createTool(ToolDto tool) {
        return Constructor.buildResponseObject(HttpStatus.OK, tool);
    }
}
