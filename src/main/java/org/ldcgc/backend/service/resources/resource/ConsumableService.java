package org.ldcgc.backend.service.resources.resource;

import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ConsumableService {
    ResponseEntity<?> getConsumable(Integer consumableId);

    ResponseEntity<?> createConsumable(ConsumableDto consumable);

    ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String filterString);

    ResponseEntity<?> updateConsumable(ConsumableDto consumableDto);

    ResponseEntity<?> deleteConsumable(Integer consumableId);
}
