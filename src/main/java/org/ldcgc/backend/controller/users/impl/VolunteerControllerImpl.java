package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.VolunteerController;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.service.users.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class VolunteerControllerImpl implements VolunteerController {

    private final VolunteerService volunteerService;

    public ResponseEntity<?> getMyVolunteer(String token) throws ParseException {
        return volunteerService.getMyVolunteer(token);
    }

    public ResponseEntity<?> getVolunteer(String volunteerId) {
        return volunteerService.getVolunteer(volunteerId);
    }

    public ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId) {
        return volunteerService.listVolunteers(pageIndex, size, filterString, builderAssistantId);
    }

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto) {
        return volunteerService.createVolunteer(volunteerDto);
    }

    public ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto vovolunteerDto) {
        return volunteerService.updateVolunteer(volunteerId, vovolunteerDto);
    }

    public ResponseEntity<?> deleteVolunteer(String volunteerId) {
        return volunteerService.deleteVolunteer(volunteerId);
    }
}
