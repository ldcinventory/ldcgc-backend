package org.ldcgc.backend.service.history;

import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface ConsumableRegisterService {

    ResponseEntity<?> getConsumableRegister(String registerId);
    ResponseEntity<?> listConsumableRegister(Integer pageIndex, Integer size, String builderAssistantId, String consumableBarcode, LocalDate dateFrom, LocalDate dateTo, String sortField);
    ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto);
    ResponseEntity<?> updateConsumableRegister(String registerId, ConsumableRegisterDto consumableRegisterDto);
    ResponseEntity<?> deleteConsumableRegister(String registerId);

}
