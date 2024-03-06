package org.ldcgc.backend.service.history;

import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.springframework.http.ResponseEntity;

public interface ToolRegisterService {

    ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto);
    ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String sortString, Boolean descOrder, String filterString, String volunteer, String tool);
    ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto);
    ResponseEntity<?> getRegister(Integer registerId);
    ResponseEntity<?> deleteRegister(Integer registerId);
}
