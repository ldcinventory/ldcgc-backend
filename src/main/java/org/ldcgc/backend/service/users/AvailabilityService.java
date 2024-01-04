package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface AvailabilityService {

    // my
    ResponseEntity<?> getMyAvailability(String token);
    ResponseEntity<?> updateMyAvailability(String token, AvailabilityDto availabilityDto);
    ResponseEntity<?> clearMyAvailability(String token);

    // managed
    ResponseEntity<?> getAvailability(String builderAssistantId);
    ResponseEntity<?> updateAvailability(String builderAssistantId, AvailabilityDto availabilityDto);
    ResponseEntity<?> clearAvailability(String builderAssistantId);

}
