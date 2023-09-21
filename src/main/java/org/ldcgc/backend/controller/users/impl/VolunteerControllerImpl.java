package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.VolunteerController;
import org.ldcgc.backend.service.users.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VolunteerControllerImpl implements VolunteerController {

    private VolunteerService volunteerService;

    public ResponseEntity<?> createUser() {
        return null;
    }

    public ResponseEntity<?> listUsers(String pageIndex, String sizeIndex, String filterString, String barcode) {
        return null;
    }

    public ResponseEntity<?> getUser(String volunteerId, String barcode) {
        return null;
    }

    public ResponseEntity<?> updateUser(String volunteerId) {
        return null;
    }

    public ResponseEntity<?> deleteUser(String volunteerId) {
        return null;
    }
}
