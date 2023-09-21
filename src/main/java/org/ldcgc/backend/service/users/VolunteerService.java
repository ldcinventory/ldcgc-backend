package org.ldcgc.backend.service.users;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface VolunteerService {

    ResponseEntity<?> createUser();

    ResponseEntity<?> listUsers(String pageIndex, String sizeIndex, String filterString, String barcode);

    ResponseEntity<?> getUser(String volunteerId);

    ResponseEntity<?> updateUser(String volunteerId);

    ResponseEntity<?> deleteUser(String volunteerId);
}
