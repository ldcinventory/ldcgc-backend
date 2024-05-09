package org.ldcgc.backend.service.resources.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.service.resources.common.impl.BrandServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith(MockitoExtension.class)
class BrandServiceImplTest {

    @InjectMocks private BrandServiceImpl brandService;

    @Mock private BrandRepository brandRepository;

    // list all
    @Test
    void getBrandsReturnEmptyList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void getBrandsReturnList() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // create
    @Test
    void createBrandsReturnBrandsExists() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void createBrandsReturnBrandsCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // delete
    @Test
    void deleteBrandsReturnBrandsNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void deleteBrandsReturnBrandsLocked() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void deleteBrandsReturnBrandsDeleted() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
