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

    public ResponseEntity<?> listMyAbsences(String token, LocalDate dateFrom, LocalDate dateTo, String sortField) {
        return absenceService.listMyAbsences(token, dateFrom, dateTo, sortField);
    }

    public ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto) {
        return absenceService.createMyAbsence(token, absenceDto);
    }

    public ResponseEntity<?> updateMyAbsence(String token, Integer absenceId, AbsenceDto absenceDto) {
        return absenceService.updateMyAbsence(token, absenceId, absenceDto);
    }

    public ResponseEntity<?> deleteMyAbsence(String token, Integer absenceId) {
        return absenceService.deleteMyAbsence(token, absenceId);
    }

    public ResponseEntity<?> getAbsence(Integer absenceId) {
        return absenceService.getAbsence(absenceId);
    }

    public ResponseEntity<?> listAbsences(LocalDate dateFrom, LocalDate dateTo, String[] builderAssistantIds, String sortField) {
        return absenceService.listAbsences(dateFrom, dateTo, builderAssistantIds, sortField);
    }

    public ResponseEntity<?> createAbsence(AbsenceDto absenceDto) {
        return absenceService.createAbsence(absenceDto);
    }

    public ResponseEntity<?> updateAbsence(Integer absenceId, AbsenceDto absenceDto) {
        return absenceService.updateAbsence(absenceId, absenceDto);
    }

    public ResponseEntity<?> deleteAbsence(Integer absenceId) {
        return absenceService.deleteAbsence(absenceId);
    }

}
