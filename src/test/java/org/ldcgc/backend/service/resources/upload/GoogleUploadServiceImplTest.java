package org.ldcgc.backend.service.resources.upload;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.service.resources.upload.impl.GoogleUploadServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
class GoogleUploadServiceImplTest {

    @InjectMocks private GoogleUploadServiceImpl googleUploadService;

    @Mock private ToolRepository toolRepository;
    @Mock private ConsumableRepository consumableRepository;

    // Upload

    @Test
    void uploadToolImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadToolImagesThrowImageQualityDefOutOfRange() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadToolImagesCleaningPreviousReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadToolImagesCleaningPreviousReturnWarningAndCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadToolImagesThrowToolNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadConsumableImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadConsumableImagesThrowImageQualityDefOutOfRange() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadConsumableImagesCleaningPreviousReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadConsumableImagesCleaningPreviousReturnWarningAndCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void uploadConsumableImagesThrowConsumableNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // Detach

    @Test
    void cleanToolImagesWithEmptyImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanAllToolImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanSpecificToolImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanToolImagesThrowToolNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanConsumableImagesWithEmptyImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanAllConsumableImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanSpecificConsumableImagesReturnCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void cleanConsumableImagesThrowToolNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
