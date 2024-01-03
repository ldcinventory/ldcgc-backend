package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface VolunteerService {

    ResponseEntity<?> getMyVolunteer(String token) throws ParseException;

    ResponseEntity<?> getVolunteer(String volunteerId);

    ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId);

    ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto);

    ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto volunteer);

    ResponseEntity<?> deleteVolunteer(String volunteerId);

}
