package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AvailabilityService {

    // my
    ResponseEntity<?> getMyAvailability(String token);
    ResponseEntity<?> updateMyAvailability(String token, AvailabilityDto availabilityDto);
    ResponseEntity<?> clearMyAvailability(String token);

    // managed
    ResponseEntity<?> getAvailability(Integer builderAssistantId);
    ResponseEntity<?> updateAvailability(Integer builderAssistantId, AvailabilityDto availabilityDto);
    ResponseEntity<?> clearAvailability(Integer builderAssistantId);

}
