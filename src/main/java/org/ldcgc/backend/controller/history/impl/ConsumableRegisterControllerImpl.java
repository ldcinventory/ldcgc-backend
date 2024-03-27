package org.ldcgc.backend.controller.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.history.ConsumableRegisterController;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ConsumableRegisterControllerImpl implements ConsumableRegisterController {

    private final ConsumableRegisterService consumableRegisterService;

    public ResponseEntity<?> getConsumableRegister(Integer registerId) {
        return consumableRegisterService.getConsumableRegister(registerId);
    }

    public ResponseEntity<?> listConsumableRegister(Integer pageIndex, Integer size, String volunteer, String consumable, LocalDateTime registerFrom, LocalDateTime registerTo, ERegisterStatus status, String sortField, boolean descOrder) {
        return consumableRegisterService.listConsumableRegister(pageIndex, size, volunteer, consumable, registerFrom, registerTo, status, sortField, descOrder);
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
}
