package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.controller.resources.GoogleUploadController;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.service.resources.upload.GoogleUploadService;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
public class GoogleUploadControllerImpl implements GoogleUploadController {

    private final GoogleUploadService googleUploadService;

    public ResponseEntity<?> uploadImages(String toolBarcode, String consumableBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException {
        if(ObjectUtils.allNotNull(toolBarcode, consumableBarcode))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.UPLOAD_IMAGES_TOO_MANY_ARGUMENTS);

        if(toolBarcode != null)
            return googleUploadService.uploadToolImages(toolBarcode, cleanExisting, images);
        if(consumableBarcode != null)
            return googleUploadService.uploadConsumableImages(consumableBarcode, cleanExisting, images);

        throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.UPLOAD_IMAGES_TOO_FEW_ARGUMENTS);
    }

    public ResponseEntity<?> detachImages(String toolBarcode, String consumableBarcode, String[] imageIds) throws GeneralSecurityException {
        if(ObjectUtils.allNotNull(toolBarcode, consumableBarcode))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.UPLOAD_IMAGES_TOO_MANY_ARGUMENTS);

        if(toolBarcode != null)
            return googleUploadService.cleanToolImages(toolBarcode, imageIds);
        if(consumableBarcode != null)
            return googleUploadService.cleanConsumableImages(consumableBarcode, imageIds);

        throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.UPLOAD_IMAGES_TOO_FEW_ARGUMENTS);
    }
}
