package org.ldcgc.backend.service.history;

import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.springframework.http.ResponseEntity;

public interface ToolRegisterService {

    ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto);
    ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String filterString);
    ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto);
}
