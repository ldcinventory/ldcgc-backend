package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.users.AvailabilityRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AvailabilityService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final JwtUtils jwtUtils;
    private final VolunteerRepository volunteerRepository;
    private final AvailabilityRepository availabilityRepository;

    public ResponseEntity<?> getMyAvailability(String token) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateMyAvailability(String token, AvailabilityDto availabilityDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> clearMyAvailability(String token) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> getAvailability(Integer volunteerId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateAvailability(Integer volunteerId, AvailabilityDto availabilityDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> clearAvailability(Integer volunteerId) {
        return Constructor.generic501();
    }
}
