package org.ldcgc.backend.controller.users.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.AbsenceController;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.service.users.AbsenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class AbsenceControllerImpl implements AbsenceController {

    private final AbsenceService absenceService;

    public ResponseEntity<?> getMyAbsence(String token, Integer absenceId) {
        return absenceService.getMyAbsence(token, absenceId);
    }

    public ResponseEntity<?> listMyAbsences(String token, LocalDate dateFrom, LocalDate dateTo) {
        return absenceService.listMyAbsences(token, dateFrom, dateTo);
    }

    public ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto) {
        return absenceService.createMyAbsence(token, absenceDto);
    }

    public ResponseEntity<?> updateMyAbsence(String token, AbsenceDto absenceDto) {
        return absenceService.updateMyAbsence(token, absenceDto);
    }

    public ResponseEntity<?> clearMyAbsence(String token, Integer absenceId) {
        return absenceService.clearMyAbsence(token, absenceId);
    }

    public ResponseEntity<?> getAbsence(Integer volunteerId) {
        return absenceService.getAbsence(volunteerId);
    }

    public ResponseEntity<?> listAbsences(String token, LocalDate dateFrom, LocalDate dateTo, Integer[] volunteerIds) {
        return absenceService.listAbsences(token, dateFrom, dateTo, volunteerIds);
    }

    public ResponseEntity<?> createAbsence(Integer volunteerId, AbsenceDto absenceDto) {
        return absenceService.createAbsence(volunteerId, absenceDto);
    }

    public ResponseEntity<?> updateAbsence(Integer volunteerId, AbsenceDto absenceDto) {
        return absenceService.updateAbsence(volunteerId, absenceDto);
    }

    public ResponseEntity<?> deleteAbsence(Integer volunteerId, Integer absenceId) {
        return absenceService.deleteAbsence(volunteerId, absenceId);
    }

}
