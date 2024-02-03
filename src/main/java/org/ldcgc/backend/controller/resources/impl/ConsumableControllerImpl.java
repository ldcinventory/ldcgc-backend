package org.ldcgc.backend.controller.resources.impl;

import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConsumableControllerImpl implements ConsumableController{
    @Autowired
    private ConsumableService consumableService;

    @Autowired
    private CategoryService categoryService;

    public ResponseEntity<?> getConsumable(Integer consumableId) {
        return consumableService.getConsumable(consumableId);
    }

    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        return consumableService.createConsumable(consumable);
    }

    @Override
    public ResponseEntity<?> updateConsumable(ConsumableDto consumable) {
        return consumableService.updateConsumable(consumable);
    }

    @Override
    public ResponseEntity<?> listConsumables(Integer page, Integer size, String sortField, String filter) {
        return consumableService.listConsumables(page, size, sortField, filter);
    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        return consumableService.deleteConsumable(consumableId);
    }
    @Override
    public ResponseEntity<?> loadExcel(MultipartFile file){
        return consumableService.loadExcel(file);
    }
}
