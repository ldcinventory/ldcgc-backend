package org.ldcgc.backend.controller.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.history.ToolRegisterController;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.service.history.ToolRegisterService;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ToolRegisterControllerImpl implements ToolRegisterController {

    private final ToolRegisterService service;

    public ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto) {
        return service.createToolRegister(toolRegisterDto);
    }

    public ResponseEntity<?> getAllToolRegisters(Integer pageIndex, Integer size, String sortString, Boolean descOrder, ERegisterStatus status, String volunteer, String tool) {
        return service.getAllToolRegisters(pageIndex, size, sortString, descOrder, status, volunteer, tool);
    }

    public ResponseEntity<?> updateToolRegister(Integer registerId, ToolRegisterDto registerDto) {
        return service.updateToolRegister(registerId, registerDto);
    }

    public ResponseEntity<?> getToolRegister(Integer registerId) {
        return service.getToolRegister(registerId);
    }

    public ResponseEntity<?> deleteToolRegister(Integer registerId) {
        return service.deleteToolRegister(registerId);
    }

    public ResponseEntity<?> createMultipleToolRegisters(List<ToolRegisterDto> toolRegistersDto) { return service.createMultipleToolRegisters(toolRegistersDto); }
}
