package org.ldcgc.backend.service.resources.consumable.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.category.impl.CategoryServiceImpl;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.groups.impl.GroupServiceImpl;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.location.impl.LocationServiceImpl;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConsumableServiceImplTest {

    // repository
    @Mock private CategoryRepository categoryRepository;
    @Mock private ConsumableRepository consumableRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private GroupRepository groupRepository;

    // service
    private CategoryService categoryService;
    private LocationService locationService;
    private GroupsService groupsService;
    private ConsumableService consumableService;

    @BeforeEach
    void init() {
        categoryService = new CategoryServiceImpl(categoryRepository);
        locationService = new LocationServiceImpl(locationRepository);
        groupsService = new GroupServiceImpl(groupRepository);
        consumableService = new ConsumableServiceImpl(consumableRepository, categoryService, locationService, groupsService);
    }

    //TODO: ACABAR LOS TESTS DEL SERVICIO CON COBERTURA DE 75% O MÁS
    @Test
    void getConsumable() {

    }

    @Test
    void createConsumable() {
    }

    // TODO este test va a la capa controller
    @Test
    void listConsumables() {
        /*
        int pageIndex = 0;
        int sizeIndex = 10;
        String sortField = "";
        String filter = "";
        controller.listConsumables(pageIndex, sizeIndex, sortField, filter);
        verify(consumableService, times(1)).listConsumables(pageIndex, sizeIndex, sortField, filter);
        */
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
