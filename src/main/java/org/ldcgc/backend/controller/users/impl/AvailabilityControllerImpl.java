package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.AvailabilityController;
import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.ldcgc.backend.service.users.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AvailabilityControllerImpl implements AvailabilityController {

    private final AvailabilityService availabilityService;

    public ResponseEntity<?> getMyAvailability(String token) {
        return availabilityService.getMyAvailability(token);
    }

    public ResponseEntity<?> updateMyAvailability(String token, AvailabilityDto availabilityDto) {
        return availabilityService.updateMyAvailability(token, availabilityDto);
    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        return availabilityService.clearMyAvailability(token);
    }

    public ResponseEntity<?> getAvailability(Integer builderAssistantId) {
        return availabilityService.getAvailability(builderAssistantId);
    }

    public ResponseEntity<?> updateAvailability(Integer builderAssistantId, AvailabilityDto availabilityDto) {
        return availabilityService.updateAvailability(builderAssistantId, availabilityDto);
    }

    public ResponseEntity<?> clearAvailability(Integer builderAssistantId) {
        return availabilityService.clearAvailability(builderAssistantId);
    }
}
