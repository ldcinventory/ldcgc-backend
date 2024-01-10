package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.VolunteerController;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.service.users.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class VolunteerControllerImpl implements VolunteerController {

    private final VolunteerService volunteerService;

    public ResponseEntity<?> getMyVolunteer(String token) throws ParseException {
        return volunteerService.getMyVolunteer(token);
    }

    public ResponseEntity<?> getVolunteer(String builderAssistantId) {
        return volunteerService.getVolunteer(builderAssistantId);
    }

    public ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId) {
        return volunteerService.listVolunteers(pageIndex, size, filterString, builderAssistantId);
    }

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto) {
        return volunteerService.createVolunteer(volunteerDto);
    }

    public ResponseEntity<?> updateVolunteer(String builderAssistantId, VolunteerDto vovolunteerDto) {
        return volunteerService.updateVolunteer(builderAssistantId, vovolunteerDto);
    }

    public ResponseEntity<?> deleteVolunteer(String builderAssistantId) {
        return volunteerService.deleteVolunteer(builderAssistantId);
    }

    public ResponseEntity<?> uploadVolunteers(Integer groupId, MultipartFile document) {
        return volunteerService.uploadVolunteers(groupId, document);
    }
}
