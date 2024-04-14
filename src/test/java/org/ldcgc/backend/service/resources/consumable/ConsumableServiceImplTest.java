package org.ldcgc.backend.service.resources.consumable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.group.impl.GroupServiceImpl;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.location.impl.LocationServiceImpl;
import org.ldcgc.backend.service.resources.consumable.impl.ConsumableExcelServiceImpl;
import org.ldcgc.backend.service.resources.consumable.impl.ConsumableServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsumableServiceImplTest {

    // repository
    @Mock private LocationRepository locationRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ResourceTypeRepository resourceTypeRepository;
    @Mock private ConsumableRepository consumableRepository;
    @Mock private GroupRepository groupRepository;

    // service
    private ConsumableExcelService consumableExcelService;
    private ConsumableService consumableService;
    private LocationService locationService;
    private GroupService groupService;

    @BeforeEach
    void init() {
        consumableExcelService = new ConsumableExcelServiceImpl(consumableRepository, brandRepository, resourceTypeRepository, locationService, groupService);
        locationService = new LocationServiceImpl(locationRepository);
        groupService = new GroupServiceImpl(groupRepository);
        consumableService = new ConsumableServiceImpl(consumableRepository, brandRepository, resourceTypeRepository, locationRepository, groupRepository, consumableExcelService);
    }

    @Test
    void whenGetConsumable_returnConsumableNotFound() {
    }

    @Test
    void whenGetConsumable_returnConsumable() {
    }

    @Test
    void whenCreateConsumable_returnConsumableIdShouldntBePresent() {
    }

    @Test
    void whenCreateConsumable_returnConsumableBarcodeExists() {
    }

    @Test
    void whenCreateConsumable_returnBrandNotFound() {
    }

    @Test
    void whenCreateConsumable_returnResourceTypeNotFound() {
    }

    @Test
    void whenCreateConsumable_returnLocationNotFound() {
    }

    @Test
    void whenCreateConsumable_returnGroupNotFound() {
    }

    @Test
    void whenCreateConsumable_returnConsumableCreated() {
    }

    @Test
    void whenListConsumables_returnPageIndexRequestExceededTotal() {
    }

    @Test
    void whenListConsumablesUnfiltered_returnConsumables() {
    }

    @Test
    void whenListConsumablesFiltered_returnConsumables() {
    }

    @Test
    void whenListConsumablesLoose_returnPageIndexRequestExceededTotal() {
    }

    @Test
    void whenListConsumablesLooseUnfiltered_returnConsumables() {
    }

    @Test
    void whenListConsumablesLooseFiltered_returnConsumables() {
    }

    @Test
    void whenUpdateConsumable_returnConsumableBarcodeUsedManyTimes() {
    }

    @Test
    void whenUpdateConsumable_returnConsumableBarcodeExists() {
    }

    @Test
    void whenUpdateConsumable_returnBrandNotFound() {
    }

    @Test
    void whenUpdateConsumable_returnResourceTypeNotFound() {
    }

    @Test
    void whenUpdateConsumable_returnLocationNotFound() {
    }

    @Test
    void whenUpdateConsumable_returnGroupNotFound() {
    }

    @Test
    void whenUpdateConsumable_returnConsumableUpdated() {
    }

    @Test
    void whenDeleteConsumable_returnConsumableNotFound() {
    }

    @Test
    void whenDeleteConsumable_returnConsumableDeleted() {
    }

    @Test
    void whenLoadExcel_returnIOExceptionFromExcelProcess() {
    }

    @Test
    void whenLoadExcel_returnConsumablesUploaded() {
    }

}
