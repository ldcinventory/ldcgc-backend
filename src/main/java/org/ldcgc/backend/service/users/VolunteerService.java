package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.util.common.EOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

@Service
public interface VolunteerService {

    ResponseEntity<?> getMyVolunteer(String token) throws ParseException;

    ResponseEntity<?> getVolunteer(String builderAssistantId);

    ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto);

    ResponseEntity<?> listVolunteers(String builderAssistantId, String filterString, Boolean isActive, Integer pageIndex, Integer size, String sortField, EOrder order);

    ResponseEntity<?> updateVolunteer(String builderAssistantId, VolunteerDto volunteer);

    ResponseEntity<?> deleteVolunteer(String builderAssistantId, Boolean confirmDeletion);

    ResponseEntity<?> uploadVolunteers(Integer groupId, MultipartFile document);

}
