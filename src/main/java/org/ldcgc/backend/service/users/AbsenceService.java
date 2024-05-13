package org.ldcgc.backend.service.users;

import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface AbsenceService {

    // my
    ResponseEntity<?> getMyAbsence(String token, Integer absenceId);
    ResponseEntity<?> listMyAbsences(String token, Integer pageIndex, Integer size, LocalDate dateFrom, LocalDate dateTo, String sortField);
    ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto);
    ResponseEntity<?> updateMyAbsence(String token, Integer absenceId, AbsenceDto absenceDto);
    ResponseEntity<?> deleteMyAbsence(String token, Integer absenceId);

    // managed
    ResponseEntity<?> getAbsence(Integer absenceId);
    ResponseEntity<?> listAbsences(Integer pageIndex, Integer size, LocalDate dateFrom, LocalDate dateTo, List<String> builderAssistantIds, String sortField, boolean groupedByBAId);
    ResponseEntity<?> createAbsence(AbsenceDto absenceDto);
    ResponseEntity<?> updateAbsence(Integer absenceId, AbsenceDto absenceDto);
    ResponseEntity<?> deleteAbsence(Integer absenceId);

}
