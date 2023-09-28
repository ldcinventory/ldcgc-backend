package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.tools.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ToolControllerImpl implements ToolController {

    @Autowired
    ToolService service;

    public ResponseEntity<?> createTool(ToolDto tool) {
        return service.createTool(tool);
    }
}
