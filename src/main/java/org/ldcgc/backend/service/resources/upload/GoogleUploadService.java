package org.ldcgc.backend.service.resources.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public interface GoogleUploadService {

    ResponseEntity<?> uploadToolImages(String toolBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException;
    ResponseEntity<?> uploadConsumableImages(String consumableBarcode, boolean cleanExisting, MultipartFile[] images) throws GeneralSecurityException, IOException;

    ResponseEntity<?> cleanToolImages(String toolBarcode, String[] imageIds) throws GeneralSecurityException;
    ResponseEntity<?> cleanConsumableImages(String consumableBarcode, String[] imageIds) throws GeneralSecurityException;

}
