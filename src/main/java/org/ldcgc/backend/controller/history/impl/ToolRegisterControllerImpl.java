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

    public ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String sortString, Boolean descOrder, ERegisterStatus status, String volunteer, String tool) {
        return service.getAllRegisters(pageIndex, size, sortString, descOrder, status, volunteer, tool);
    }

    public ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto) {
        return service.updateRegister(registerId, registerDto);
    }

    public ResponseEntity<?> getRegister(Integer registerId) {
        return service.getRegister(registerId);
    }

    public ResponseEntity<?> deleteRegister(Integer registerId) {
        return service.deleteRegister(registerId);
    }

    public ResponseEntity<?> createToolRegisters(List<ToolRegisterDto> toolRegistersDto) { return service.createToolRegisters(toolRegistersDto); }
}
