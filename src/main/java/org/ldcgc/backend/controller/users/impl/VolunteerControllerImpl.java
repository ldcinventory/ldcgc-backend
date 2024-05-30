package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.VolunteerController;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.service.users.VolunteerService;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.EVStatus;
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

    public ResponseEntity<?> createVolunteer(VolunteerDto volunteerDto) {
        return volunteerService.createVolunteer(volunteerDto);
    }

    public ResponseEntity<?> listVolunteers(String builderAssistantId, String filterString, EVStatus status, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return volunteerService.listVolunteers(builderAssistantId, filterString, status, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> updateVolunteer(String builderAssistantId, VolunteerDto volunteerDto) {
        return volunteerService.updateVolunteer(builderAssistantId, volunteerDto);
    }

    public ResponseEntity<?> deleteVolunteer(String builderAssistantId, Boolean confirmDeletion) {
        return volunteerService.deleteVolunteer(builderAssistantId, confirmDeletion);
    }

    public ResponseEntity<?> uploadVolunteers(Integer groupId, MultipartFile document) {
        return volunteerService.uploadVolunteers(groupId, document);
    }
}
