package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.VolunteerController;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.service.users.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VolunteerControllerImpl implements VolunteerController {

    private final VolunteerService volunteerService;

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteer) {
        return volunteerService.createVolunteer(volunteer);
    }

    public ResponseEntity<?> listVolunteers(Integer pageIndex, Integer size, String filterString, String builderAssistantId) {
        return volunteerService.listVolunteers(pageIndex, size, filterString, builderAssistantId);
    }

    public ResponseEntity<?> getVolunteer(String volunteerId) {
        return volunteerService.getVolunteer(volunteerId);
    }

    public ResponseEntity<?> updateVolunteer(String volunteerId, VolunteerDto volunteer) {
        return volunteerService.updateVolunteer(volunteerId, volunteer);
    }

    public ResponseEntity<?> deleteVolunteer(String volunteerId) {
        return volunteerService.deleteVolunteer(volunteerId);
    }
}
