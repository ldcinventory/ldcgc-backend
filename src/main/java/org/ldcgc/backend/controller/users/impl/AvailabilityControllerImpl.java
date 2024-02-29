package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.AvailabilityController;
import org.ldcgc.backend.service.users.AvailabilityService;
import org.ldcgc.backend.util.common.EWeekday;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AvailabilityControllerImpl implements AvailabilityController {

    private final AvailabilityService availabilityService;

    public ResponseEntity<?> getMyAvailability(String token) {
        return availabilityService.getMyAvailability(token);
    }

    public ResponseEntity<?> updateMyAvailability(String token, List<EWeekday> availability) {
        return availabilityService.updateMyAvailability(token, availability);
    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        return availabilityService.clearMyAvailability(token);
    }

    public ResponseEntity<?> getAvailability(String builderAssistantId) {
        return availabilityService.getAvailability(builderAssistantId);
    }

    public ResponseEntity<?> updateAvailability(String builderAssistantId, List<EWeekday> availability) {
        return availabilityService.updateAvailability(builderAssistantId, availability);
    }

    public ResponseEntity<?> clearAvailability(String builderAssistantId) {
        return availabilityService.clearAvailability(builderAssistantId);
    }
}
