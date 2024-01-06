package org.ldcgc.backend.service.users.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.users.Absence;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.payload.mapper.users.AbsenceMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AbsenceService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class AbsenceServiceImpl implements AbsenceService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final VolunteerRepository volunteerRepository;
    private final AbsenceRepository absenceRepository;

    // my

    public ResponseEntity<?> getMyAbsence(String token, Integer absenceId) {
        Volunteer volunteer = getVolunteerFromToken(token);

        Absence absence = volunteer.getAbsences().stream()
            .filter(ab -> ab.getId().equals(absenceId))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, AbsenceMapper.MAPPER.toDto(absence));
    }

    public ResponseEntity<?> listMyAbsences(String token, LocalDate dateFrom, LocalDate dateTo) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return listAbsences(dateFrom, dateTo, new String[]{volunteer.getBuilderAssistantId()});
    }

    public ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return createAbsence(volunteer, absenceDto);
    }

    public ResponseEntity<?> updateMyAbsence(String token, Integer absenceId, AbsenceDto absenceDto) {
        Volunteer volunteer = getVolunteerFromToken(token);

        volunteer.getAbsences().stream()
            .filter(absence -> absence.getId().equals(absenceId))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND));

        return updateAbsence(absenceId, absenceDto);
    }

    public ResponseEntity<?> deleteMyAbsence(String token, Integer absenceId) {
        Volunteer volunteer = getVolunteerFromToken(token);

        volunteer.getAbsences().stream()
            .filter(absence -> absence.getId().equals(absenceId))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND));

        return deleteAbsence(absenceId);
    }

    private Volunteer getVolunteerFromToken(String token) {
        try {
            User user = userRepository.findById(jwtUtils.getUserIdFromStringToken(token)).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

            return Optional.ofNullable(user.getVolunteer()).orElseThrow(
                () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_DOESNT_HAVE_VOLUNTEER));

        } catch (ParseException ignore) {
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.TOKEN_NOT_PARSEABLE);
        }

    }

    // managed

    public ResponseEntity<?> getAbsence(Integer absenceId) {
        Absence absence = absenceRepository.findById(absenceId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, AbsenceMapper.MAPPER.toDto(absence));
    }

    public ResponseEntity<?> listAbsences(LocalDate dateFrom, LocalDate dateTo, String[] builderAssistantIds) {
        // absence (entity), query (sql), cb (criteriaBuilder)
        //List<Volunteer> volunteers = volunteerRepository.findAll((Specification<Volunteer>) (absence, query, cb) -> {
        List<Absence> absences = absenceRepository.findAll((Specification<Absence>) (absence, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(dateFrom != null)
                predicates.add(cb.and(cb.greaterThanOrEqualTo(absence.get("dateFrom"), dateFrom)));

            if(dateTo != null)
                predicates.add(cb.and(cb.lessThanOrEqualTo(absence.get("dateTo"), dateFrom)));

            if(builderAssistantIds != null)
                predicates.add(filterByBuilderAssistantIds(absence, cb, builderAssistantIds));

            return cb.and(predicates.toArray(new Predicate[0]));

        });

        List<AbsenceDto> absencesDto = absences.stream().map(AbsenceMapper.MAPPER::toDto).toList();

        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), absencesDto);
    }

    private Predicate filterByBuilderAssistantIds(Root<?> absence, CriteriaBuilder cb, String[] builderAssistantIds) {
        Join<Volunteer, Absence> volunteerAbsence = absence.join("volunteer", JoinType.LEFT);
        String baIDs = String.format("'%s'", String.join(",", builderAssistantIds));
        //return cb.and(cb.in(Arrays.stream(builderAssistantIds).toList()));
        return null;

    }

    public ResponseEntity<?> createAbsence(String builderAssistantId, AbsenceDto absenceDto) {
        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(builderAssistantId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        return createAbsence(volunteer, absenceDto);
    }

    private ResponseEntity<?> createAbsence(Volunteer volunteer, AbsenceDto absenceDto) {
        Absence absence = AbsenceMapper.MAPPER.toEntity(absenceDto);
        absence.setVolunteer(volunteer);
        volunteer.getAbsences().add(absence);

        absence = absenceRepository.save(absence);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.ABSENCE_CREATED, AbsenceMapper.MAPPER.toDto(absence));
    }

    public ResponseEntity<?> updateAbsence(Integer absenceId, AbsenceDto absenceDto) {
        Absence absence = absenceRepository.findById(absenceId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_NOT_FOUND));

        AbsenceMapper.MAPPER.update(absence, absenceDto);

        absence = absenceRepository.saveAndFlush(absence);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.ABSENCE_UPDATED, AbsenceMapper.MAPPER.toDto(absence));
    }

    public ResponseEntity<?> deleteAbsence(Integer absenceId) {
        Absence absence = absenceRepository.findById(absenceId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_NOT_FOUND));

        absenceRepository.delete(absence);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.ABSENCE_DELETED);
    }

}
