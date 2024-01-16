package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.payload.mapper.users.VolunteerMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.AvailabilityServiceImpl;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.common.EWeekday;
import org.ldcgc.backend.util.retrieving.Messages;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.ldcgc.backend.base.mock.MockedAbsencesAvailability.getRandomAvailabilityList;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class AvailabilityServiceImplTest {

    AvailabilityService availabilityService;

    @Mock VolunteerRepository volunteerRepository;
    @Mock UserRepository userRepository;
    @Mock JwtUtils jwtUtils;

    private String mockedToken;

    @BeforeEach
    public void init() {
        availabilityService = new AvailabilityServiceImpl(jwtUtils, userRepository, volunteerRepository);
        mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDto(ERole.ROLE_USER)));
    }

    private final User USER_WITHOUT_VOLUNTEER = UserMapper.MAPPER.toEntity(MockedUserVolunteer.getRandomMockedUserDtoWithoutVolunteer());
    private final User USER_WITH_VOLUNTEER = MockedUserVolunteer.getRandomMockedUser();

    // me

    @Test
    public void whenGetMyAvailability_returnNullByParseException() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.getMyAvailability(mockedToken));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAvailability_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.getMyAvailability(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAvailability_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.getMyAvailability(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenGetMyAvailability_returnVolunteerWithoutBAId() throws ParseException {

        final User USER_WITH_VOLUNTEER_NO_BA_ID = MockedUserVolunteer.getRandomMockedUser();
        USER_WITH_VOLUNTEER_NO_BA_ID.getVolunteer().setBuilderAssistantId(null);

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER_NO_BA_ID)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.getMyAvailability(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_WITHOUT_BA_ID, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());
    }

    @Test
    public void whenGetMyAvailability_returnAvailability() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);

        ResponseEntity<?> response = availabilityService.getMyAvailability(mockedToken);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(USER_WITH_VOLUNTEER.getVolunteer());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(volunteerDto.getAvailability(), responseBody.getData());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAvailability_returnNullByParseException() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.updateMyAvailability(mockedToken, getRandomAvailabilityList()));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAvailability_returnUserNotFound() throws ParseException {

        List<EWeekday> availability = getRandomAvailabilityList();

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.updateMyAvailability(mockedToken, availability));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAvailability_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);
        List<EWeekday> availability = getRandomAvailabilityList();

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.updateMyAvailability(mockedToken, availability));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAvailability_returnVolunteerWithoutBAId() throws ParseException {

        final User USER_WITH_VOLUNTEER_NO_BA_ID = MockedUserVolunteer.getRandomMockedUser();
        USER_WITH_VOLUNTEER_NO_BA_ID.getVolunteer().setBuilderAssistantId(null);
        List<EWeekday> availability = getRandomAvailabilityList();

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER_NO_BA_ID)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.updateMyAvailability(mockedToken, availability));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_WITHOUT_BA_ID, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenUpdateMyAvailability_returnAvailabilityUpdated() throws ParseException {

        List<EWeekday> availability = USER_WITH_VOLUNTEER.getVolunteer().getAvailability().stream().toList();

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        doReturn(USER_WITH_VOLUNTEER.getVolunteer()).when(volunteerRepository).saveAndFlush(USER_WITH_VOLUNTEER.getVolunteer());

        ResponseEntity<?> response = availabilityService.updateMyAvailability(mockedToken, availability);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        VolunteerDto volunteerDto = VolunteerMapper.MAPPER.toDto(USER_WITH_VOLUNTEER.getVolunteer());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(responseBody.getMessage(), Messages.Info.AVAILABILITY_UPDATED);
        assertEquals(volunteerDto.getAvailability(), responseBody.getData());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(volunteerRepository, atMostOnce()).saveAndFlush(any(Volunteer.class));
    }

    @Test
    public void whenClearMyAvailability_returnNullByParseException() throws ParseException {

        doThrow(ParseException.class).when(jwtUtils).getUserIdFromStringToken(mockedToken);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.clearMyAvailability(mockedToken));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_PARSEABLE, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenClearMyAvailability_returnUserNotFound() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.clearMyAvailability(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenClearMyAvailability_returnUserDoesntHaveVolunteer() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITHOUT_VOLUNTEER)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.clearMyAvailability(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_DOESNT_HAVE_VOLUNTEER, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenClearMyAvailability_returnVolunteerWithoutBAId() throws ParseException {

        final User USER_WITH_VOLUNTEER_NO_BA_ID = MockedUserVolunteer.getRandomMockedUser();
        USER_WITH_VOLUNTEER_NO_BA_ID.getVolunteer().setBuilderAssistantId(null);

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER_NO_BA_ID)).when(userRepository).findById(0);

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.clearMyAvailability(mockedToken));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_WITHOUT_BA_ID, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(anyInt());

    }

    @Test
    public void whenClearMyAvailability_returnAvailabilityCleared() throws ParseException {

        doReturn(0).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(USER_WITH_VOLUNTEER)).when(userRepository).findById(0);
        Volunteer volunteer = USER_WITH_VOLUNTEER.getVolunteer();
        doReturn(volunteer).when(volunteerRepository).saveAndFlush(volunteer);

        ResponseEntity<?> response = availabilityService.clearMyAvailability(mockedToken);
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(responseBody.getMessage(), Messages.Info.AVAILABILITY_CLEARED);
        assertEquals(Collections.emptyList(), responseBody.getData());

        verify(userRepository, atMostOnce()).findById(anyInt());
        verify(volunteerRepository, atMostOnce()).saveAndFlush(any(Volunteer.class));

    }

    // managed

    @Test
    public void whenGetAvailability_returnVolunteerNotFound() {

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId("12345");

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.updateAvailability("12345", getRandomAvailabilityList()));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

    }

    @Test
    public void whenGetAvailability_returnAvailability() {

        final Volunteer volunteer = USER_WITH_VOLUNTEER.getVolunteer();
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findByBuilderAssistantId("12345");

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

        ResponseEntity<?> response = availabilityService.getAvailability("12345");
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(volunteer.getAvailability().stream().toList(), responseBody.getData());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());
        verify(volunteerRepository, atMostOnce()).saveAndFlush(any(Volunteer.class));

    }

    @Test
    public void whenUpdateAvailability_returnVolunteerNotFound() {

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId("12345");

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.getAvailability("12345"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

    }

    @Test
    public void whenUpdateAvailability_returnAvailabilityUpdated() {

        final Volunteer volunteer = USER_WITH_VOLUNTEER.getVolunteer();
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findByBuilderAssistantId("12345");
        doReturn(volunteer).when(volunteerRepository).saveAndFlush(volunteer);

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

        ResponseEntity<?> response = availabilityService.updateAvailability("12345", getRandomAvailabilityList());
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(responseBody.getMessage(), Messages.Info.AVAILABILITY_UPDATED);
        assertEquals(volunteer.getAvailability().stream().toList(), responseBody.getData());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());
        verify(volunteerRepository, atMostOnce()).saveAndFlush(any(Volunteer.class));

    }

    @Test
    public void whenClearAvailability_returnVolunteerNotFound() {

        doReturn(Optional.empty()).when(volunteerRepository).findByBuilderAssistantId("12345");

        RequestException ex = assertThrows(RequestException.class, () -> availabilityService.clearAvailability("12345"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.VOLUNTEER_NOT_FOUND, ex.getMessage());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

    }

    @Test
    public void whenClearAvailability_returnAvailabilityCleared() {

        final Volunteer volunteer = USER_WITH_VOLUNTEER.getVolunteer();
        doReturn(Optional.of(volunteer)).when(volunteerRepository).findByBuilderAssistantId("12345");
        doReturn(volunteer).when(volunteerRepository).saveAndFlush(volunteer);

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());

        ResponseEntity<?> response = availabilityService.clearAvailability("12345");
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody.getData());
        assertEquals(responseBody.getMessage(), Messages.Info.AVAILABILITY_CLEARED);
        assertEquals(Collections.emptyList(), responseBody.getData());

        verify(volunteerRepository, atMostOnce()).findByBuilderAssistantId(anyString());
        verify(volunteerRepository, atMostOnce()).saveAndFlush(any(Volunteer.class));

    }

}
