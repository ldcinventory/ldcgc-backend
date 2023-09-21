package org.ldcgc.backend.service.users;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public interface VolunteerService {

    ResponseEntity<?> createUser();

    ResponseEntity<?> listUsers(
            @RequestParam(required = false) String pageIndex,
            @RequestParam(required = false) String sizeIndex,
            @RequestParam(required = false) String filterString,
            @RequestParam(required = false) String barcode);

    ResponseEntity<?> getUser(
            @RequestParam(required = false) String volunteerId,
            @RequestParam(required = false) String barcode);

    ResponseEntity<?> updateUser(
            @PathVariable String volunteerId);

    ResponseEntity<?> deleteUser(
            @PathVariable String volunteerId);
}
