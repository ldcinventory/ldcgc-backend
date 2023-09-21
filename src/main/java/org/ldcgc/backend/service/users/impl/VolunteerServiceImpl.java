package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private VolunteerRepository volunteerRepository;

    public ResponseEntity<?> createUser() {
        return Constructor.generic501();
    }

    public ResponseEntity<?> listUsers(String pageIndex, String sizeIndex, String filterString, String barcode) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> getUser(String volunteerId, String barcode) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateUser(String volunteerId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> deleteUser(String volunteerId) {
        return Constructor.generic501();
    }
}
