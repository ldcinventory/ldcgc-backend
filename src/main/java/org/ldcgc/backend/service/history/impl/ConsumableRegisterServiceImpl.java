package org.ldcgc.backend.service.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ConsumableRegisterServiceImpl implements ConsumableRegisterService {

    private final ConsumableRegisterRepository consumableRegisterRepository;

    public ResponseEntity<?> getConsumableRegister(String registerId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> listConsumableRegister(Integer pageIndex, Integer size, String builderAssistantId, String consumableBarcode, LocalDate dateFrom, LocalDate dateTo, String sortField) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateConsumableRegister(String registerId, ConsumableRegisterDto consumableRegisterDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> deleteConsumableRegister(String registerId) {
        return Constructor.generic501();
    }
}
