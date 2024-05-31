package org.ldcgc.backend.service.resources.consumable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.group.impl.GroupServiceImpl;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.location.impl.LocationServiceImpl;
import org.ldcgc.backend.service.resources.common.BrandService;
import org.ldcgc.backend.service.resources.consumable.impl.ConsumableExcelServiceImpl;
import org.ldcgc.backend.service.resources.consumable.impl.ConsumableServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
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
    @Mock private BrandService brandService;

    @BeforeEach
    void init() {
        consumableExcelService = new ConsumableExcelServiceImpl(consumableRepository, brandRepository, resourceTypeRepository, locationService, groupService);
        locationService = new LocationServiceImpl(locationRepository, groupRepository);
        groupService = new GroupServiceImpl(groupRepository);
        consumableService = new ConsumableServiceImpl(consumableRepository, brandRepository, resourceTypeRepository, locationRepository, groupRepository, brandService, consumableExcelService);
    }

    @Test
    void whenGetConsumable_returnConsumableNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenGetConsumable_returnConsumable() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnConsumableIdShouldntBePresent() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnConsumableBarcodeExists() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnBrandNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnResourceTypeNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnLocationNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnGroupNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumable_returnConsumableCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumables_returnPageIndexRequestExceededTotal() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumablesUnfiltered_returnConsumables() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumablesFiltered_returnConsumables() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumablesLoose_returnPageIndexRequestExceededTotal() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumablesLooseUnfiltered_returnConsumables() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumablesLooseFiltered_returnConsumables() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnConsumableBarcodeUsedManyTimes() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnConsumableBarcodeExists() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnBrandNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnResourceTypeNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnLocationNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnGroupNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumable_returnConsumableUpdated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenDeleteConsumable_returnConsumableNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenDeleteConsumable_returnConsumableDeleted() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenLoadExcel_returnIOExceptionFromExcelProcess() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenLoadExcel_returnConsumablesUploaded() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
