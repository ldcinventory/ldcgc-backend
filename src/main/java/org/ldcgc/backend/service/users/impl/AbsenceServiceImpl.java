package org.ldcgc.backend.service.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AbsenceService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AbsenceServiceImpl implements AbsenceService {

    private final JwtUtils jwtUtils;
    private final VolunteerRepository volunteerRepository;
    private final AbsenceRepository absenceRepository;

    public ResponseEntity<?> getMyAbsence(String token, Integer absenceId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> listMyAbsences(String token, LocalDate dateFrom, LocalDate dateTo) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateMyAbsence(String token, AbsenceDto absenceDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> clearMyAbsence(String token, Integer absenceId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> getAbsence(Integer volunteerId) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> listAbsences(String token, LocalDate dateFrom, LocalDate dateTo, Integer[] volunteerIds) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> createAbsence(Integer volunteerId, AbsenceDto absenceDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> updateAbsence(Integer volunteerId, AbsenceDto absenceDto) {
        return Constructor.generic501();
    }

    public ResponseEntity<?> deleteAbsence(Integer volunteerId, Integer absenceId) {
        return Constructor.generic501();
    }

}
