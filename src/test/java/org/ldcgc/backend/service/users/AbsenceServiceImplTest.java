package org.ldcgc.backend.service.users;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.users.Absence;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.AbsenceRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.payload.mapper.users.AbsenceMapper;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.AbsenceServiceImpl;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomBuilderAssistantId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class AbsenceServiceImplTest {

    private AbsenceService absenceService;

    @Mock JwtUtils jwtUtils;
    @Mock UserRepository userRepository;
    @Mock VolunteerRepository volunteerRepository;
    @Mock AbsenceRepository absenceRepository;

    // -- lambda part ->>
    @Mock EntityManager entityManager;
    @Mock CriteriaBuilder criteriaBuilder;
    @Mock CriteriaQuery<Absence> criteriaQuery;
    @Mock Root<Absence> rootAbsence;
    @Mock Join<Volunteer, Absence> joinVolunteerAbsence;
    @Mock Path<String> builderAssistantIdExpression;
    // -- lambda part <<-

    private String mockedToken;

    @BeforeEach
    public void init() {
        absenceService = new AbsenceServiceImpl(jwtUtils, userRepository, volunteerRepository, absenceRepository);
        mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDto(ERole.ROLE_USER)));

    }

    private final User USER_WITHOUT_VOLUNTEER = UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDtoWithoutVolunteer());
    private final User USER_WITH_VOLUNTEER = MockedUserVolunteer.getRandomMockedUser();
    final LocalDate DATE_FROM = LocalDate.of(2024, 1, 1);
    final LocalDate DATE_TO = LocalDate.of(2024, 2, 1);
    final AbsenceDto ABSENCE = AbsenceDto.builder().id(1).dateFrom(DATE_FROM).dateTo(DATE_TO).builderAssistantId(getRandomBuilderAssistantId()).build();
    final String SORT_FIELD = "dateFrom";

    // me
    @Test
    public void whenGetMyAbsence_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.getMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAbsence_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.getMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAbsence_returnTokenNotParseable() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.getMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, Mockito.times(0)).findById(anyInt());

    }

    @Test
    public void whenGetMyAbsence_returnVolunteerAbsencesEmpty() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        USER_WITH_VOLUNTEER.getVolunteer().setAbsences(null);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.getMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_ABSENCES_EMPTY, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAbsence_returnVolunteerAbsenceNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        Absence ABSENCE = AbsenceMapper.MAPPER.toEntity(this.ABSENCE);
        USER_WITH_VOLUNTEER.getVolunteer().setAbsences(Collections.singletonList(ABSENCE));

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.getMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAbsence_returnAbsence() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        final Absence absenceExpected = USER_WITH_VOLUNTEER.getVolunteer().getAbsences().getFirst();
        final int absenceId = absenceExpected.getId();

        ResponseEntity<?> response = absenceService.getMyAbsence(mockedToken, absenceId);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getData());
        assertThat(AbsenceMapper.MAPPER.toDto(absenceExpected)).usingRecursiveComparison().isEqualTo(responseBody.getData());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenListMyAbsences_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.listMyAbsences(mockedToken, DATE_FROM, DATE_TO, "dateFrom"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenListMyAbsences_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.listMyAbsences(mockedToken, DATE_FROM, DATE_TO, SORT_FIELD));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenListMyAbsences_returnTokenNotParseable() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.listMyAbsences(mockedToken, DATE_FROM, DATE_TO, SORT_FIELD));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, Mockito.times(0)).findById(anyInt());

    }

    @Test
    public void whenListMyAbsences_returnVolunteerAbsencesEmpty() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        USER_WITH_VOLUNTEER.getVolunteer().setAbsences(null);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.listMyAbsences(mockedToken, DATE_FROM, DATE_TO, SORT_FIELD));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_ABSENCES_EMPTY, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenListMyAbsencesUnfiltered_returnUserAbsences() throws ParseException {

        List<Absence> absences = USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences.forEach(absence -> absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer()));
        List<AbsenceDto> absencesDto = USER_WITH_VOLUNTEER.getVolunteer().getAbsences().stream().map(AbsenceMapper.MAPPER::toDto).toList();

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        doReturn(absences).when(absenceRepository).findAll(any(Specification.class));

        ResponseEntity<?> response = absenceService.listMyAbsences(mockedToken, null, null, SORT_FIELD);
        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertNotNull(responseBody);
        HashMap<String, List<AbsenceDto>> absencesMap = (HashMap<String, List<AbsenceDto>>) responseBody.getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), responseBody.getMessage());
        assertTrue(absencesMap.containsKey(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        assertEquals(absencesDto.size(), absencesMap.get(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).findAll(any(Specification.class));

    }

    @Test
    public void whenListMyAbsencesFilteredByDate_returnUserAbsencesFiltered() throws ParseException {

        List<Absence> absences = USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences.forEach(absence -> absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer()));
        List<AbsenceDto> absencesDto = USER_WITH_VOLUNTEER.getVolunteer().getAbsences().stream().map(AbsenceMapper.MAPPER::toDto).toList();

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        doReturn(absences).when(absenceRepository).findAll(any(Specification.class));

        LocalDate dateFrom = absencesDto.getFirst().getDateFrom();
        LocalDate dateTo = absencesDto.getLast().getDateTo();

        ResponseEntity<?> response = absenceService.listMyAbsences(mockedToken, dateFrom, dateTo, SORT_FIELD);
        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertNotNull(responseBody);
        HashMap<String, List<AbsenceDto>> absencesMap = (HashMap<String, List<AbsenceDto>>) responseBody.getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), responseBody.getMessage());
        assertTrue(absencesMap.containsKey(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        assertEquals(absencesDto.size(), absencesMap.get(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).findAll(any(Specification.class));

    }

    @Test
    public void whenCreateMyAbsence_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.createMyAbsence(mockedToken, ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenCreateMyAbsence_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.createMyAbsence(mockedToken, ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenCreateMyAbsence_returnTokenNotParseable() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.createMyAbsence(mockedToken, ABSENCE));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, Mockito.times(0)).findById(anyInt());

    }

    @Test
    public void whenCreateMyAbsence_returnAbsenceCreated() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        AbsenceDto ABSENCE = this.ABSENCE.toBuilder().builderAssistantId(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).build();
        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE);
        absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer());
        doReturn(absence).when(absenceRepository).saveAndFlush(any(Absence.class));

        ResponseEntity<?> response = absenceService.createMyAbsence(mockedToken, ABSENCE);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getData());
        assertEquals(Messages.Info.ABSENCE_CREATED, responseBody.getMessage());
        assertThat(ABSENCE).usingRecursiveComparison().isEqualTo(responseBody.getData());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).save(any(Absence.class));

    }

    @Test
    public void whenUpdateMyAbsence_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.updateMyAbsence(mockedToken, 0, ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAbsence_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.updateMyAbsence(mockedToken, 0, ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAbsence_returnTokenNotParseable() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.updateMyAbsence(mockedToken, 0, ABSENCE));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, Mockito.times(0)).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAbsence_returnVolunteerAbsencesEmpty() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        USER_WITH_VOLUNTEER.getVolunteer().setAbsences(null);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.updateMyAbsence(mockedToken, 0, ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_ABSENCES_EMPTY, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAbsence_returnVolunteerAbsenceNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        Absence ABSENCE = AbsenceMapper.MAPPER.toEntity(this.ABSENCE);
        USER_WITH_VOLUNTEER.getVolunteer().setAbsences(Collections.singletonList(ABSENCE));

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.updateMyAbsence(mockedToken, 0, this.ABSENCE));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAbsence_returnAbsenceUpdated() throws ParseException {

        Absence absenceEntity = USER_WITH_VOLUNTEER.getVolunteer().getAbsences().getFirst();
        AbsenceDto ABSENCE = AbsenceMapper.MAPPER.toDto(absenceEntity)
            .toBuilder().builderAssistantId(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId())
            .build();
        absenceEntity.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer());

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        doReturn(absenceEntity).when(absenceRepository).saveAndFlush(any(Absence.class));

        ResponseEntity<?> response = absenceService.updateMyAbsence(mockedToken, ABSENCE.getId(), ABSENCE);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getData());
        assertEquals(Messages.Info.ABSENCE_UPDATED, responseBody.getMessage());
        assertThat(ABSENCE).usingRecursiveComparison().isEqualTo(responseBody.getData());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).save(any(Absence.class));

    }

    @Test
    public void whenDeleteMyAbsence_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenDeleteMyAbsence_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenDeleteMyAbsence_returnTokenNotParseable() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, Mockito.times(0)).findById(anyInt());

    }

    @Test
    public void whenDeleteMyAbsence_returnVolunteerAbsencesEmpty() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        USER_WITH_VOLUNTEER.getVolunteer().setAbsences(null);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteMyAbsence(mockedToken, 0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_ABSENCES_EMPTY, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenDeleteMyAbsence_returnVolunteerAbsenceNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE);
        absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer());
        doReturn(absence).when(absenceRepository).save(any(Absence.class));

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteMyAbsence(mockedToken, -1));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.ABSENCE_VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenDeleteMyAbsence_returnAbsenceDeleted() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE);
        absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer());
        doReturn(absence).when(absenceRepository).save(any(Absence.class));
        final int absenceId = USER_WITH_VOLUNTEER.getVolunteer().getAbsences().getFirst().getId();

        ResponseEntity<?> response = absenceService.deleteMyAbsence(mockedToken, absenceId);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getMessage());
        assertEquals(Messages.Info.ABSENCE_DELETED, responseBody.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(volunteerRepository, atMostOnce()).save(any(Volunteer.class));
        verify(absenceRepository, atMostOnce()).deleteById(anyInt());

    }

    // managed
    @Test
    public void whenGetAbsence_returnAbsenceNotFound() {

        doReturn(Optional.empty()).when(absenceRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.getAbsence(0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.ABSENCE_NOT_FOUND, ex.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetAbsence_returnAbsence() {

        Volunteer volunteer = Volunteer.builder().builderAssistantId(ABSENCE.getBuilderAssistantId()).build();
        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE).toBuilder().volunteer(volunteer).build();
        doReturn(Optional.of(absence)).when(absenceRepository).findById(absence.getId());

        ResponseEntity<?> response = absenceService.getAbsence(absence.getId());
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getData());
        assertThat(ABSENCE).usingRecursiveComparison().isEqualTo(responseBody.getData());

        verify(absenceRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenListAbsencesUnfiltered_returnAbsencesList() {

        List<Absence> absences1st = USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences1st.forEach(absence -> absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer()));

        final User ANOTHER_USER_WITH_VOLUNTEER = MockedUserVolunteer.getRandomMockedUser();
        List<Absence> absences2nd = ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences2nd.forEach(absence -> absence.setVolunteer(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer()));

        List<Absence> bothLists = Stream.concat(absences1st.stream(), absences2nd.stream()).toList();

        List<AbsenceDto> absencesDto = bothLists.stream().map(AbsenceMapper.MAPPER::toDto).toList();

        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        ArgumentCaptor<Specification<Absence>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        doReturn(bothLists).when(absenceRepository).findAll(specCaptor.capture());

        ResponseEntity<?> response = absenceService.listAbsences(null, null, null, SORT_FIELD);

        // -- lambda part
        List<Absence> testAbsences = testLambdaPart(specCaptor);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        HashMap<String, List<AbsenceDto>> absencesMap = (HashMap<String, List<AbsenceDto>>) responseBody.getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), responseBody.getMessage());
        assertTrue(absencesMap.containsKey(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        assertTrue(absencesMap.containsKey(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        int firstListAbsencesQt = absencesMap.get(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        int secondListAbsencesQt = absencesMap.get(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        assertEquals(absencesDto.size(), firstListAbsencesQt + secondListAbsencesQt);
        assertEquals(bothLists, testAbsences);

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, times(2)).findAll(any(Specification.class));

    }

    @Test
    public void whenListAbsencesFilteredByDate_returnAbsencesList() {

        List<Absence> absences1st = USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences1st.forEach(absence -> absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer()));

        final User ANOTHER_USER_WITH_VOLUNTEER = MockedUserVolunteer.getRandomMockedUser();
        List<Absence> absences2nd = ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences2nd.forEach(absence -> absence.setVolunteer(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer()));

        List<Absence> bothLists = Stream.concat(absences1st.stream(), absences2nd.stream()).toList();

        List<AbsenceDto> absencesDto = bothLists.stream().map(AbsenceMapper.MAPPER::toDto).toList();

        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        ArgumentCaptor<Specification<Absence>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        doReturn(bothLists).when(absenceRepository).findAll(specCaptor.capture());

        LocalDate dateFrom = absencesDto.getFirst().getDateFrom();
        LocalDate dateTo = absencesDto.getLast().getDateTo();

        ResponseEntity<?> response = absenceService.listAbsences(dateFrom, dateTo, null, SORT_FIELD);

        // -- lambda part
        List<Absence> testAbsences = testLambdaPart(specCaptor);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertNotNull(responseBody);
        HashMap<String, List<AbsenceDto>> absencesMap = (HashMap<String, List<AbsenceDto>>) responseBody.getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), responseBody.getMessage());
        assertTrue(absencesMap.containsKey(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        assertTrue(absencesMap.containsKey(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        int firstListAbsencesQt = absencesMap.get(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        int secondListAbsencesQt = absencesMap.get(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        assertEquals(absencesDto.size(), firstListAbsencesQt + secondListAbsencesQt);
        assertEquals(bothLists, testAbsences);

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, times(2)).findAll(any(Specification.class));

    }

    @Test
    public void whenListAbsencesFilteredByBAIds_returnAbsencesList() {

        List<Absence> absences1st = USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences1st.forEach(absence -> absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer()));

        final User ANOTHER_USER_WITH_VOLUNTEER = MockedUserVolunteer.getRandomMockedUser();
        List<Absence> absences2nd = ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences2nd.forEach(absence -> absence.setVolunteer(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer()));

        List<Absence> bothLists = Stream.concat(absences1st.stream(), absences2nd.stream()).toList();

        List<AbsenceDto> absencesDto = bothLists.stream().map(AbsenceMapper.MAPPER::toDto).toList();

        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        ArgumentCaptor<Specification<Absence>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        doReturn(bothLists).when(absenceRepository).findAll(specCaptor.capture());

        ResponseEntity<?> response = absenceService.listAbsences(null, null, new String[]{
            USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId(),
            ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()}, SORT_FIELD);

        // -- lambda part
        List<Absence> testAbsences = testLambdaPart(specCaptor);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertNotNull(responseBody);
        HashMap<String, List<AbsenceDto>> absencesMap = (HashMap<String, List<AbsenceDto>>) responseBody.getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), responseBody.getMessage());
        assertTrue(absencesMap.containsKey(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        assertTrue(absencesMap.containsKey(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        int firstListAbsencesQt = absencesMap.get(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        int secondListAbsencesQt = absencesMap.get(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        assertEquals(absencesDto.size(), firstListAbsencesQt + secondListAbsencesQt);
        assertEquals(bothLists, testAbsences);

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, times(2)).findAll(any(Specification.class));

    }

    @Test
    public void whenListAbsencesFilteredByDateAndBAIds_returnAbsencesList() {

        List<Absence> absences1st = USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences1st.forEach(absence -> absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer()));

        final User ANOTHER_USER_WITH_VOLUNTEER = MockedUserVolunteer.getRandomMockedUser();
        List<Absence> absences2nd = ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getAbsences();
        absences2nd.forEach(absence -> absence.setVolunteer(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer()));

        List<Absence> bothLists = Stream.concat(absences1st.stream(), absences2nd.stream()).toList();

        List<AbsenceDto> absencesDto = bothLists.stream().map(AbsenceMapper.MAPPER::toDto).toList();

        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        ArgumentCaptor<Specification<Absence>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        doReturn(bothLists).when(absenceRepository).findAll(specCaptor.capture());

        LocalDate dateFrom = absencesDto.getFirst().getDateFrom();
        LocalDate dateTo = absencesDto.getLast().getDateTo();

        ResponseEntity<?> response = absenceService.listAbsences(dateFrom, dateTo, new String[]{
            USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId(),
            ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()}, SORT_FIELD);

        // -- lambda part
        List<Absence> testAbsences = testLambdaPart(specCaptor);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertNotNull(responseBody);
        HashMap<String, List<AbsenceDto>> absencesMap = (HashMap<String, List<AbsenceDto>>) responseBody.getData();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(String.format(Messages.Info.ABSENCES_FOUND, absencesDto.size()), responseBody.getMessage());
        assertTrue(absencesMap.containsKey(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        assertTrue(absencesMap.containsKey(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()));
        int firstListAbsencesQt = absencesMap.get(USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        int secondListAbsencesQt = absencesMap.get(ANOTHER_USER_WITH_VOLUNTEER.getVolunteer().getBuilderAssistantId()).size();
        assertEquals(absencesDto.size(), firstListAbsencesQt + secondListAbsencesQt);
        assertEquals(bothLists, testAbsences);

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, times(2)).findAll(any(Specification.class));

    }

    private List<Absence> testLambdaPart(ArgumentCaptor<Specification<Absence>> specCaptor) {
        when(entityManager.getCriteriaBuilder()).thenReturn(this.criteriaBuilder);
        doReturn(joinVolunteerAbsence).when(rootAbsence).join(anyString(), eq(JoinType.LEFT));
        doReturn(builderAssistantIdExpression).when(joinVolunteerAbsence).get(anyString());

        Specification<Absence> capturedSpec = specCaptor.getValue();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate predicate = capturedSpec.toPredicate(rootAbsence, criteriaQuery, criteriaBuilder);
        return absenceRepository.findAll((Specification<Absence>) (r, q, cb) -> predicate);
    }

    @Test
    public void whenCreateAbsence_returnVolunteerNotInformed() {

        AbsenceDto absenceDto = ABSENCE.toBuilder().builderAssistantId(null).build();

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.createAbsence(absenceDto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_INFORMED, ex.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenCreateAbsence_returnVolunteerNotFound() {

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId(anyString());

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.createAbsence(ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

    }

    @Test
    public void whenCreateAbsence_returnAbsenceCreated() {

        Volunteer volunteer = USER_WITH_VOLUNTEER.getVolunteer();
        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE).toBuilder().volunteer(volunteer).build();
        volunteer.getAbsences().add(absence);

        doReturn(Optional.of(absence)).when(absenceRepository).findById(absence.getId());
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findByBuilderAssistantId(ABSENCE.getBuilderAssistantId());
        doReturn(absence).when(absenceRepository).saveAndFlush(absence);

        ResponseEntity<?> response = absenceService.createAbsence(ABSENCE);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getData());
        assertEquals(Messages.Info.ABSENCE_CREATED, responseBody.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).saveAndFlush(any(Absence.class));

    }

    @Test
    public void whenUpdateAbsence_returnAbsenceNotFound() {

        doReturn(Optional.empty()).when(absenceRepository).findById(anyInt());

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.updateAbsence(0, ABSENCE));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.ABSENCE_NOT_FOUND, ex.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateAbsence_returnAbsenceUpdated() {

        AbsenceDto absenceDto = ABSENCE.toBuilder().builderAssistantId(null).build();
        Absence absence = AbsenceMapper.MAPPER.toEntity(absenceDto);

        doReturn(Optional.of(absence)).when(absenceRepository).findById(absence.getId());
        doReturn(absence).when(absenceRepository).saveAndFlush(absence);

        ResponseEntity<?> response = absenceService.updateAbsence(absence.getId(), absenceDto);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getData());
        assertEquals(Messages.Info.ABSENCE_UPDATED, responseBody.getMessage());
        assertThat(absenceDto).usingRecursiveComparison().isEqualTo(responseBody.getData());

        verify(absenceRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).saveAndFlush(any(Absence.class));

    }

    @Test
    public void whenDeleteAbsence_returnAbsenceNotFound() {

        doReturn(Optional.empty()).when(absenceRepository).findById(anyInt());

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteAbsence(0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.ABSENCE_NOT_FOUND, ex.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenDeleteAbsence_returnVolunteerFromAbsenceNotFound() {

        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE);
        absence.setVolunteer(USER_WITH_VOLUNTEER.getVolunteer());

        doReturn(Optional.of(absence)).when(absenceRepository).findById(anyInt());
        doReturn(Optional.empty()).when(volunteerRepository).findById(anyInt());

        RequestException ex = assertThrows(RequestException.class, () -> absenceService.deleteAbsence(0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_FROM_ABSENCE_NOT_FOUND, ex.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());
        verify(volunteerRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenDeleteAbsence_returnAbsenceDeleted() {

        Volunteer volunteer = USER_WITH_VOLUNTEER.getVolunteer();
        Absence absence = AbsenceMapper.MAPPER.toEntity(ABSENCE).toBuilder().volunteer(volunteer).build();
        volunteer.getAbsences().add(absence);

        doReturn(Optional.of(absence)).when(absenceRepository).findById(absence.getId());
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findById(volunteer.getId());

        ResponseEntity<?> response = absenceService.deleteAbsence(absence.getId());
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        assertNotNull(responseBody.getMessage());
        assertEquals(Messages.Info.ABSENCE_DELETED, responseBody.getMessage());

        verify(absenceRepository, atMostOnce()).findById(anyInt());
        verify(absenceRepository, atMostOnce()).delete(any(Absence.class));
        verify(volunteerRepository, atMostOnce()).findById(anyInt());
        verify(volunteerRepository, atMostOnce()).save(any(Volunteer.class));

    }

}
