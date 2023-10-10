package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ConsumableService {
    ResponseEntity<?> getConsumable(Integer consumableId);
    ResponseEntity<?> createConsumable(ConsumableDto consumable);
}
