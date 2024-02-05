package org.ldcgc.backend.controller.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.history.ToolRegisterController;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ToolRegisterControllerImpl implements ToolRegisterController {

    private final ToolRegisterService service;
    @Override
    public ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto) {
        return null;
    }

    @Override
    public ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String filterString) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto) {
        return null;
    }
}
