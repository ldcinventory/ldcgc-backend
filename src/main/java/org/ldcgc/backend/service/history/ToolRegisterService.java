package org.ldcgc.backend.service.history;

import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ToolRegisterService {

    ResponseEntity<?> createToolRegister(ToolRegisterDto toolRegisterDto);
    ResponseEntity<?> getAllToolRegisters(ERegisterStatus status, String volunteer, String tool, Integer pageIndex, Integer size, String sortString, EOrder order);
    ResponseEntity<?> updateToolRegister(Integer registerId, ToolRegisterDto registerDto);
    ResponseEntity<?> getToolRegister(Integer registerId);
    ResponseEntity<?> deleteToolRegister(Integer registerId);
    ResponseEntity<?> createMultipleToolRegisters(List<ToolRegisterDto> toolRegistersDto);

}
