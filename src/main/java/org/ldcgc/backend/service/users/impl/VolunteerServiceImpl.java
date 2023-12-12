package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteer) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> listVolunteers(Integer pageIndex, Integer sizeIndex, String filterString, String barcode) {
        return Constructor.buildResponseMessage(HttpStatus.OK, "Hola");
    }

    public ResponseEntity<?> getVolunteer(String volunteerId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto volunteer) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> deleteVolunteer(String volunteerId) {
        return Constructor.generic501();
    }

}
