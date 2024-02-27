package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.controller.resources.UploadController;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.service.resources.upload.UploadService;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
public class UploadControllerImpl implements UploadController {

    private final UploadService uploadService;

    public ResponseEntity<?> uploadImages(String toolBarcode, String consumableBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException {
        if(ObjectUtils.allNotNull(toolBarcode, consumableBarcode))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.UPLOAD_IMAGES_TOO_MANY_ARGUMENTS);

        if(toolBarcode != null)
            return uploadService.uploadToolImages(toolBarcode, cleanExisting, images);
        if(consumableBarcode != null)
            return uploadService.uploadConsumableImages(consumableBarcode, cleanExisting, images);

        throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.UPLOAD_IMAGES_TOO_FEW_ARGUMENTS);
    }
}
