package org.ldcgc.backend.service.users;

import org.ldcgc.backend.util.common.EWeekday;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AvailabilityService {

    // my
    ResponseEntity<?> getMyAvailability(String token);
    ResponseEntity<?> updateMyAvailability(String token, List<EWeekday> availability);
    ResponseEntity<?> clearMyAvailability(String token);

    // managed
    ResponseEntity<?> getAvailability(String builderAssistantId);
    ResponseEntity<?> updateAvailability(String builderAssistantId, List<EWeekday> availability);
    ResponseEntity<?> clearAvailability(String builderAssistantId);

}
