package org.ldcgc.backend.service.resources.consumable;

import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ConsumableService {

    ResponseEntity<?> getConsumable(Integer consumableId);
    ResponseEntity<?> createConsumable(ConsumableDto consumable);
    ResponseEntity<?> listConsumables(Integer pageIndex, Integer size, String category, String brand, String name, String model, String description, String sortField);
    ResponseEntity<?> listConsumablesLoose(Integer pageIndex, Integer size, String filterString, String sortField);
    ResponseEntity<?> updateConsumable(ConsumableDto consumableDto, Integer consumableId);
    ResponseEntity<?> deleteConsumable(Integer consumableId);
    ResponseEntity<?> loadExcel(Integer groupId, MultipartFile file);

}
