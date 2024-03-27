package org.ldcgc.backend.service.users.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.ldcgc.backend.db.model.users.Absence;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.payload.mapper.users.AbsenceMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AbsenceService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collections;
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

    public ResponseEntity<?> listMyAbsences(String token, Integer pageIndex, Integer size, LocalDate dateFrom, LocalDate dateTo, String sortField) {
        Volunteer volunteer = getVolunteerFromToken(token);
        validateVolunteerHasAbsences(volunteer);

        return listAbsences(pageIndex, size, dateFrom, dateTo, Collections.singletonList(volunteer.getBuilderAssistantId()), sortField, true);
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

    public ResponseEntity<?> listAbsences(Integer pageIndex, Integer size, LocalDate dateFrom, LocalDate dateTo, List<String> builderAssistantIds, String sortField, boolean groupedByBAId) {

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(sortField).ascending());

        Page<AbsenceDto> pagedAbsences = ObjectUtils.allNull(dateFrom, dateTo, builderAssistantIds)
            ? absenceRepository.findAll(pageable).map(AbsenceMapper.MAPPER::toDto)
            : absenceRepository.findAllFiltered(dateFrom, dateTo, builderAssistantIds, pageable).map(AbsenceMapper.MAPPER::toDto);

        if (pageIndex > pagedAbsences.getTotalPages())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

        if(groupedByBAId) {
            Map<String, List<AbsenceDto>> absencesDtoMap = pagedAbsences.getContent().stream().collect(Collectors.groupingBy(
                AbsenceDto::getBuilderAssistantId, Collectors.mapping(Function.identity(), Collectors.toList())
            ));

            return Constructor.buildResponseMessageObject(HttpStatus.OK,
                String.format(Messages.Info.ABSENCES_LISTED, pagedAbsences.getTotalElements()),
                PaginationDetails.fromPagingGrouped(pageable, pagedAbsences, absencesDtoMap));
        }

        return Constructor.buildResponseMessageObject(HttpStatus.OK,
            String.format(Messages.Info.ABSENCES_LISTED, pagedAbsences.getTotalElements()),
            PaginationDetails.fromPaging(pageable, pagedAbsences));

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
        volunteerRepository.saveAndFlush(volunteer);

        absenceRepository.delete(absence);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.ABSENCE_DELETED);
    }

}
