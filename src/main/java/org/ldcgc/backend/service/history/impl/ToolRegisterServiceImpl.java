package org.ldcgc.backend.service.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToolRegisterServiceImpl implements ToolRegisterService {
    @Override
    public ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto) {
        return null;
    }
}
