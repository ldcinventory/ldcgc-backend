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
    ResponseEntity<?> updateMyAbsence(String token, Integer absenceId, AbsenceDto absenceDto);
    ResponseEntity<?> deleteMyAbsence(String token, Integer absenceId);

    // managed
    ResponseEntity<?> getAbsence(Integer absenceId);
    ResponseEntity<?> listAbsences(LocalDate dateFrom, LocalDate dateTo, String[] builderAssistantIds);
    ResponseEntity<?> createAbsence(AbsenceDto absenceDto);
    ResponseEntity<?> updateAbsence(Integer absenceId, AbsenceDto absenceDto);
    ResponseEntity<?> deleteAbsence(Integer absenceId);

}
