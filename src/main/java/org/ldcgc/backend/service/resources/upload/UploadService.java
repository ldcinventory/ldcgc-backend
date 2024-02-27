package org.ldcgc.backend.service.resources.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public interface UploadService {

    ResponseEntity<?> uploadToolImages(String toolBarcode, MultipartFile[] images) throws GeneralSecurityException, IOException;
    ResponseEntity<?> uploadConsumableImages(String consumableBarcode, MultipartFile[] images) throws GeneralSecurityException, IOException;

}
