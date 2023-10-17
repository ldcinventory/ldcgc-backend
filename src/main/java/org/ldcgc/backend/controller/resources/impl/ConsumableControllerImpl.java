/*package org.ldcgc.backend.controller.resources.impl;

import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.service.resources.resource.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumableControllerImpl implements ConsumableController {

    @Autowired(required = true)
    private ConsumableService consumableService;

    @Override
    public ResponseEntity<?> testAccessWithCredentials() {
        return null;
    }

    @Override
    public ResponseEntity<?> testAccessWithAdminCredentials() {
        return null;
    }

    @Override
    public ResponseEntity<?> getConsumable(Integer consumableId) {
        return consumableService.getConsumable(consumableId);
    }

    @Override
    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        return consumableService.createConsumable(consumable);
    }

    @Override
    public ResponseEntity<?> updateConsumable(ConsumableDto consumable) {
        return consumableService.updateConsumable(consumable);
    }

    @Override
    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String filterString) {
        return consumableService.listConsumables(pageIndex, sizeIndex, filterString);
    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        return consumableService.deleteConsumable(consumableId);
    }
}*/
