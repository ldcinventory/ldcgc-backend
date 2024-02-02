package org.ldcgc.backend.service.resources.consumable.impl;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.controller.resources.impl.ConsumableControllerImpl;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ConsumableServiceImplTest {
    @InjectMocks private ConsumableController controller = new ConsumableControllerImpl();
    @Mock
    private ConsumableService consumableService;

    //TODO: ACABAR LOS TESTS DEL SERVICIO CON COBERTURA DE 75% O MÁS
    @Test
    void getConsumable() {

    }

    @Test
    void createConsumable() {
    }

    @Test
    void listConsumables() {
        int pageIndex = 0;
        int sizeIndex = 10;
        String sortField = "";
        String filter = "";
        controller.listConsumables(pageIndex, sizeIndex, sortField, filter);
        verify(consumableService, times(1)).listConsumables(pageIndex, sizeIndex, sortField, filter);
    }

    @Test
    void updateConsumable() {
    }

    @Test
    void deleteConsumable() {
    }

    @Test
    void loadExcel() {
    }
}