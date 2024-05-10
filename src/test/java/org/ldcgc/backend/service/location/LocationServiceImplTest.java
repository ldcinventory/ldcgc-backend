package org.ldcgc.backend.service.location;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.service.location.impl.LocationServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @InjectMocks private LocationServiceImpl locationService;

    @Mock private LocationRepository locationRepository;
    @Mock private GroupRepository groupRepository;

    // list all
    @Test
    void getAllLocationsReturnEmptyList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void getAllLocationsReturnList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // list
    @Test
    void getLocationsFilteredReturnEmptyList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void getLocationsFilteredReturnList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // create
    @Test
    void createLocationsReturnGroupNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void createLocationsReturnLocationsExists() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void createLocationsReturnLocationCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // create
    @Test
    void updateLocationsReturnGroupNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void updateLocationsReturnLocationNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void updateLocationsReturnLocationUpdated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // delete
    @Test
    void deleteLocationsReturnLocationNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void deleteLocationsReturnLocationsDeleted() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
