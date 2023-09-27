package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ToolControllerImpl implements ToolController {

    @Override
    public ResponseEntity<?> testAccessWithCredentials() {
        return null;
    }

    @Override
    public ResponseEntity<?> testAccessWithAdminCredentials() {
        return null;
    }

    @Override
    public ResponseEntity<?> createTool() {
        return null;
    }
}
