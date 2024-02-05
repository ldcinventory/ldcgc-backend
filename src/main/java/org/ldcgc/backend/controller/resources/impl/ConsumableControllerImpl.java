package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ConsumableControllerImpl implements ConsumableController {

    private final ConsumableService consumableService;

    public ResponseEntity<?> getConsumable(Integer consumableId) {
        return consumableService.getConsumable(consumableId);
    }

    public ResponseEntity<?> createConsumable(ConsumableDto consumableDto) {
        return consumableService.createConsumable(consumableDto);
    }

    public ResponseEntity<?> updateConsumable(ConsumableDto consumableDto, Integer consumableId) {
        return consumableService.updateConsumable(consumableDto, consumableId);
    }

    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer size, String filter, String sortField) {
        return consumableService.listConsumables(pageIndex, size, filter, sortField);
    }

    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        return consumableService.deleteConsumable(consumableId);
    }

    public ResponseEntity<?> loadExcel(Integer groupId, MultipartFile file) {
        return consumableService.loadExcel(groupId, file);
    }
}
