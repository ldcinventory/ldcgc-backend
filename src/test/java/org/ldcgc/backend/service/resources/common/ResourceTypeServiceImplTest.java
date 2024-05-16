package org.ldcgc.backend.service.resources.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.service.resources.common.impl.ResourceTypeServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
class ResourceTypeServiceImplTest {

    @InjectMocks private ResourceTypeServiceImpl resourceTypeService;

    @Mock private ResourceTypeRepository resourceTypeRepository;

    // list all
    @Test
    void getResourceTypeReturnEmptyList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void getResourceTypeReturnList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // create
    @Test
    void createResourceTypeReturnResourceTypeExists() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void createResourceTypeReturnResourceTypeCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // delete
    @Test
    void deleteResourceTypeReturnResourceTypeNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void deleteResourceTypeReturnResourceTypeLocked() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void deleteResourceTypeReturnResourceTypeDeleted() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
