package org.ldcgc.backend.service.users.impl;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        validateVolunteerHasAbsences(volunteer);

        Absence absence = volunteer.getAbsences().stream()
            .filter(ab -> ab.getId().equals(absenceId))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, AbsenceMapper.MAPPER.toDto(absence));
    }

    public ResponseEntity<?> listMyAbsences(String token, LocalDate dateFrom, LocalDate dateTo, String sortField) {
        Volunteer volunteer = getVolunteerFromToken(token);
        validateVolunteerHasAbsences(volunteer);

        return listAbsences(dateFrom, dateTo, new String[]{volunteer.getBuilderAssistantId()}, sortField);
    }

    public ResponseEntity<?> createMyAbsence(String token, AbsenceDto absenceDto) {
        Volunteer volunteer = getVolunteerFromToken(token);

        return createAbsence(volunteer, AbsenceMapper.MAPPER.toEntity(absenceDto));
    }

    public ResponseEntity<?> updateMyAbsence(String token, Integer absenceId, AbsenceDto absenceDto) {
        Volunteer volunteer = getVolunteerFromToken(token);
        validateVolunteerHasAbsences(volunteer);

        Absence absenceEntity = volunteer.getAbsences().stream()
            .filter(absence -> absence.getId().equals(absenceId))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND));

        return updateAbsence(absenceEntity, absenceDto);
    }

    public ResponseEntity<?> deleteMyAbsence(String token, Integer absenceId) {
        Volunteer volunteer = getVolunteerFromToken(token);
        validateVolunteerHasAbsences(volunteer);

        Absence absence = volunteer.getAbsences().stream()
            .filter(ab -> ab.getId().equals(absenceId))
            .findFirst()
            .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND));

        return deleteAbsence(volunteer, absence);
    }

    private void validateVolunteerHasAbsences(Volunteer volunteer) {
        if(CollectionUtils.isEmpty(volunteer.getAbsences()))
            throw new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_ABSENCES_EMPTY);
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

    public ResponseEntity<?> listAbsences(LocalDate dateFrom, LocalDate dateTo, String[] builderAssistantIds, String sortField) {
        List<Absence> absences = absenceRepository.findAll((Specification<Absence>) (absence, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if(dateFrom != null)
                predicates.add(cb.greaterThanOrEqualTo(absence.get("dateFrom"), dateFrom));

            if(dateTo != null)
                predicates.add(cb.lessThanOrEqualTo(absence.get("dateTo"), dateTo));

            if (builderAssistantIds != null && builderAssistantIds.length > 0) {
                Join<Volunteer, Absence> volunteerAbsenceJoin = absence.join("volunteer", JoinType.LEFT);
                Expression<String> builderAssistantIdExpression = volunteerAbsenceJoin.get("builderAssistantId");
                predicates.add(builderAssistantIdExpression.in(builderAssistantIds));
            }

            if (predicates.isEmpty())
                return null;

            query.orderBy(cb.asc(absence.get(sortField)));

            return cb.and(predicates.toArray(new Predicate[0]));

        });

        List<AbsenceDto> absencesDtoList = absences.stream().map(AbsenceMapper.MAPPER::toDto).toList();
        Map<String, List<AbsenceDto>> absencesDtoMap = absencesDtoList.stream().collect(Collectors.groupingBy(
            absenceDto -> absenceDto.getBuilderAssistantId(),
            Collectors.mapping(Function.identity(), Collectors.toList())
        ));

        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(Messages.Info.ABSENCES_FOUND, absencesDtoList.size()), absencesDtoMap);
    }

    public ResponseEntity<?> createAbsence(AbsenceDto absenceDto) {
        if(absenceDto.getBuilderAssistantId() == null)
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.VOLUNTEER_NOT_INFORMED);

        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(absenceDto.getBuilderAssistantId()).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        return createAbsence(volunteer, AbsenceMapper.MAPPER.toEntity(absenceDto));
    }

    public ResponseEntity<?> updateAbsence(Integer absenceId, AbsenceDto absenceDto) {
        Absence absenceEntity = absenceRepository.findById(absenceId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_NOT_FOUND));

        return updateAbsence(absenceEntity, absenceDto);
    }

    public ResponseEntity<?> deleteAbsence(Integer absenceId) {
        Absence absence = absenceRepository.findById(absenceId).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.ABSENCE_NOT_FOUND));

        Volunteer volunteer = volunteerRepository.findById(absence.getVolunteer().getId()).orElseThrow(
            () -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_FROM_ABSENCE_NOT_FOUND)
        );

        return deleteAbsence(volunteer, absence);
    }

    private ResponseEntity<?> createAbsence(Volunteer volunteer, Absence absence) {

        absence.setVolunteer(volunteer);
        volunteer.getAbsences().add(absence);

        absence = absenceRepository.saveAndFlush(absence);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.ABSENCE_CREATED, AbsenceMapper.MAPPER.toDto(absence));
    }

    private ResponseEntity<?> updateAbsence(Absence absenceEntity, AbsenceDto absenceDto) {

        AbsenceMapper.MAPPER.update(absenceEntity, absenceDto);

        absenceEntity = absenceRepository.saveAndFlush(absenceEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.ABSENCE_UPDATED, AbsenceMapper.MAPPER.toDto(absenceEntity));

    }

    private ResponseEntity<?> deleteAbsence(Volunteer volunteer, Absence absence) {
        //detach from volunteer absences
        volunteer.getAbsences().remove(absence);
        volunteerRepository.save(volunteer);

        absenceRepository.delete(absence);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.ABSENCE_DELETED);
    }

}
