package org.ldcgc.backend.service.users;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface VolunteerService {

    ResponseEntity<?> createVolunteer();

    ResponseEntity<?> listVolunteers(String pageIndex, String sizeIndex, String filterString, String barcode);

    ResponseEntity<?> getVolunteer(String volunteerId);

    ResponseEntity<?> updateVolunteer(String volunteerId);

    ResponseEntity<?> deleteVolunteer(String volunteerId);
}
