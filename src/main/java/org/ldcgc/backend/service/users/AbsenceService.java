package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface AbsenceService {

    // my
    ResponseEntity<?> getMyAbsence(String token, Integer absenceId);
    ResponseEntity<?> listMyAbsences(String token, LocalDate dateFrom, LocalDate dateTo);
    ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto);
    ResponseEntity<?> updateMyAbsence(String token, AbsenceDto absenceDto);
    ResponseEntity<?> clearMyAbsence(String token, Integer absenceId);

    // managed
    ResponseEntity<?> getAbsence(Integer volunteerId);
    ResponseEntity<?> listAbsences(String token, LocalDate dateFrom, LocalDate dateTo, Integer[] volunteerIds);
    ResponseEntity<?> createAbsence(Integer volunteerId, AbsenceDto absenceDto);
    ResponseEntity<?> updateAbsence(Integer volunteerId, AbsenceDto absenceDto);
    ResponseEntity<?> deleteAbsence(Integer volunteerId, Integer absenceId);

}
