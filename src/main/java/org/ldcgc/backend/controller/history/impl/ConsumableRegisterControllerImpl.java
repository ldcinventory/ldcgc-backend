package org.ldcgc.backend.controller.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.history.ConsumableRegisterController;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
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

    public ResponseEntity<?> listConsumableRegister(Integer pageIndex, Integer size, String builderAssistantId, String consumableBarcode, LocalDateTime dateFrom, LocalDateTime dateTo, String sortField) {
        return consumableRegisterService.listConsumableRegister(pageIndex, size, builderAssistantId, consumableBarcode, dateFrom, dateTo, sortField);
    }

    public ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto) {
        return consumableRegisterService.createConsumableRegister(consumableRegisterDto);
    }

    public ResponseEntity<?> updateConsumableRegister(Integer registerId, ConsumableRegisterDto consumableRegisterDto) {
        return consumableRegisterService.updateConsumableRegister(registerId, consumableRegisterDto);
    }

    public ResponseEntity<?> deleteConsumableRegister(Integer registerId) {
        return consumableRegisterService.deleteConsumableRegister(registerId);
    }
}
