package org.ldcgc.backend.controller.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.history.ConsumableRegisterController;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
import org.ldcgc.backend.shared.enums.EOrder;
import org.ldcgc.backend.shared.enums.ERegisterStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ConsumableRegisterControllerImpl implements ConsumableRegisterController {

    private final ConsumableRegisterService consumableRegisterService;

    public ResponseEntity<?> getConsumableRegister(Integer registerId) {
        return consumableRegisterService.getConsumableRegister(registerId);
    }

    public ResponseEntity<?> listConsumableRegister(String volunteer, String consumable, LocalDateTime registerFrom, LocalDateTime registerTo, ERegisterStatus status, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return consumableRegisterService.listConsumableRegister(volunteer, consumable, registerFrom, registerTo, status, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto) {
        return consumableRegisterService.createConsumableRegister(consumableRegisterDto);
    }

    public ResponseEntity<?> updateConsumableRegister(Integer registerId, ConsumableRegisterDto consumableRegisterDto) {
        return consumableRegisterService.updateConsumableRegister(registerId, consumableRegisterDto);
    }

    public ResponseEntity<?> deleteConsumableRegister(Integer registerId, boolean undoStockChanges) {
        return consumableRegisterService.deleteConsumableRegister(registerId, undoStockChanges);
    }

    public ResponseEntity<?> createMultipleConsumableRegisters(List<ConsumableRegisterDto> consumableRegistersDto) {
        return consumableRegisterService.createMultipleConsumableRegisters(consumableRegistersDto);
    }
}
