package org.ldcgc.backend.service.history;

import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ToolRegisterService {

    ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto);
    ResponseEntity<?> getAllRegisters(Integer pageIndex, Integer size, String sortString, Boolean descOrder, ERegisterStatus filterString, String volunteer, String tool);
    ResponseEntity<?> updateRegister(Integer registerId, ToolRegisterDto registerDto);
    ResponseEntity<?> getRegister(Integer registerId);
    ResponseEntity<?> deleteRegister(Integer registerId);
    ResponseEntity<?> createToolRegisters(List<ToolRegisterDto> toolRegistersDto);
}
