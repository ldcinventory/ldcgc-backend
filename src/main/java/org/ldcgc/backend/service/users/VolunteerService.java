package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

@Service
public interface VolunteerService {

    ResponseEntity<?> getMyVolunteer(String token) throws ParseException;

    ResponseEntity<?> getVolunteer(String builderAssistantId);

    ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId, String sortField);

    ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto);

    ResponseEntity<?> updateVolunteer(String builderAssistantId, VolunteerDto volunteer);

    ResponseEntity<?> deleteVolunteer(String builderAssistantId);

    ResponseEntity<?> uploadVolunteers(Integer groupId, MultipartFile document);
}
