package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface VolunteerService {

    ResponseEntity<?> createVolunteer(VolunteerDto volunteer);

    ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId);

    ResponseEntity<?> getMyVolunteer(String token) throws ParseException;

    ResponseEntity<?> getVolunteer(String volunteerId);

    ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto volunteer);

    ResponseEntity<?> deleteVolunteer(String volunteerId);

}
