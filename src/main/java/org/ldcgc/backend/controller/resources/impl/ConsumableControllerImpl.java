package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.ldcgc.backend.shared.enums.EOrder;
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

    public ResponseEntity<?> listConsumables(String barcode, String category, String brand, String name, String model, String description, String location, Boolean hasStock, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return consumableService.listConsumables(barcode, category, brand, name, model, description, location, hasStock, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> listConsumablesLoose(String filterString, Boolean hasStock, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return consumableService.listConsumablesLoose(filterString, hasStock, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> updateConsumable(ConsumableDto consumableDto, Integer consumableId) {
        return consumableService.updateConsumable(consumableDto, consumableId);
    }

    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        return consumableService.deleteConsumable(consumableId);
    }

    public ResponseEntity<?> uploadExcelConsumables(MultipartFile file, Integer initialRow) {
        return consumableService.uploadExcelConsumables(file, initialRow);
    }

    public ResponseEntity<?> uploadGSheetTemplate(String url, Integer initialRow) {
        return consumableService.uploadGSheetTemplate(url, initialRow);
    }
}
