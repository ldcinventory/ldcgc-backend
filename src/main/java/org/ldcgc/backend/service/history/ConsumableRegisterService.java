package org.ldcgc.backend.service.history;

import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface ConsumableRegisterService {

    ResponseEntity<?> getConsumableRegister(Integer registerId);
    ResponseEntity<?> listConsumableRegister(String volunteer, String consumable, LocalDateTime registerFrom, LocalDateTime registerTo, ERegisterStatus status, Integer pageIndex, Integer size, String sortField, EOrder order);
    ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto);
    ResponseEntity<?> updateConsumableRegister(Integer registerId, ConsumableRegisterDto consumableRegisterDto);
    ResponseEntity<?> deleteConsumableRegister(Integer registerId, boolean undoStockChanges);
    ResponseEntity<?> createMultipleConsumableRegisters(List<ConsumableRegisterDto> consumableRegistersDto);

}
