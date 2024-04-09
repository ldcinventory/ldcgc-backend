package org.ldcgc.backend.service.resources.consumable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.mock.MockedResources;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.location.LocationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ConsumableExcelServiceImplTest {

    @InjectMocks private ConsumableExcelService consumableExcelService;

    @Mock private ConsumableRepository consumableRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ResourceTypeRepository resourceTypeRepository;
    @Mock private LocationService locationService;
    @Mock private GroupService groupService;

    private List<ConsumableDto> consumablesDto;
    private List<Consumable> consumablesDB;
    private List<Brand> brandsDB;
    private List<ResourceType> resourceTypesDB;
    private List<LocationDto> locationsDto;
    private List<GroupDto> groupsDto;

    @BeforeEach
    void init() {
        consumablesDto = IntStream.range(0, 10).boxed().map(x ->
            MockedResources.getRandomConsumableDto()).toList();

        consumablesDB = consumablesDto.stream().map(ConsumableMapper.MAPPER::toMo).toList();
        brandsDB = consumablesDB.stream().map(Consumable::getBrand).toList();
        resourceTypesDB = consumablesDB.stream().map(Consumable::getResourceType).toList();

        groupsDto = consumablesDB.stream().map(Consumable::getGroup).map(GroupMapper.MAPPER::toDto).toList();
        locationsDto = consumablesDB.stream().map(Consumable::getLocation).map(LocationMapper.MAPPER::toDto).toList();

        doReturn(consumablesDB).when(consumableRepository).findAll();
        doReturn(brandsDB).when(brandRepository).findAll();
        doReturn(resourceTypesDB).when(resourceTypeRepository).findAll();
        doReturn(locationsDto).when(locationService).getAllLocations();
        doReturn(groupsDto).when(groupService).getAllGroups();

    }

    @Test
    void whenUploadConsumablesFromExcel_returnExcelParseError() {
        fail("Not yet implemented");
    }

    @Test
    void whenUploadConsumablesFromExcel_returnConsumables() {
        fail("Not yet implemented");
    }

    @Test
    void whenUploadConsumablesFromExcel_returnLocationNotFound() {
        fail("Not yet implemented");
    }

    @Test
    void whenUploadConsumablesFromExcel_returnGroupNotFound() {
        fail("Not yet implemented");
    }

    @Test
    void whenUploadConsumablesFromExcel_returnConsumablesNewBrand() {
        fail("Not yet implemented");
    }

    @Test
    void whenUploadConsumablesFromExcel_returnConsumablesNewResourceType() {
        fail("Not yet implemented");
    }

}
