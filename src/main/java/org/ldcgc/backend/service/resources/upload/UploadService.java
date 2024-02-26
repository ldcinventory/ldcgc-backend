package org.ldcgc.backend.service.resources.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UploadService {

    ResponseEntity<?> uploadToolImages(String toolBarcode, MultipartFile[] images);
    ResponseEntity<?> uploadConsumableImages(String consumableBarcode, MultipartFile[] images);

}
