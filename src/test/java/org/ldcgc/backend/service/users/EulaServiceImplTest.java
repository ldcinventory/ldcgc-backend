package org.ldcgc.backend.service.users;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.EulaDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.EulaServiceImpl;
import org.ldcgc.backend.util.common.EEULAStatus;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.retrieving.Messages;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.ldcgc.backend.base.mock.MockedUserDetails.getRandomMockedUserDto;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
class EulaServiceImplTest {

    private EulaService eulaService;
    @Mock private TokenRepository tokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private JwtUtils jwtUtils;

    private String mockedToken;

    @BeforeEach
    public void init() {
        eulaService = new EulaServiceImpl(userRepository, tokenRepository, jwtUtils);
        mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(getRandomMockedUserDto()));
    }

    // mocked users
    private final User USER_NOT_FOUND = User.builder().id(0).role(ERole.ROLE_USER).build();
    private final User USER_STANDARD_EULA_NOT_ACCEPTED = User.builder().id(1).role(ERole.ROLE_USER).build();
    private final User USER_STANDARD_EULA_ACCEPTED = User.builder().id(2).role(ERole.ROLE_USER).acceptedEULA(LocalDateTime.now()).build();
    private final User USER_MANAGER_EULA_STANDARD_NOT_ACCEPTED = User.builder().id(3).role(ERole.ROLE_MANAGER).build();
    private final User USER_MANAGER_EULA_STANDARD_ACCEPTED_EULA_MANAGER_NOT = User.builder().id(4).role(ERole.ROLE_MANAGER).acceptedEULA(LocalDateTime.now()).build();
    private final User USER_MANAGER_EULA_STANDARD_AND_MANAGER_ACCEPTED = User.builder().id(5).role(ERole.ROLE_MANAGER).acceptedEULA(LocalDateTime.now()).acceptedEULAManager(LocalDateTime.now()).build();

    // getEula

    @Test
    public void whenGetEula_returnUserNotFound() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.NOT_FOUND));
        log.info(String.format("Expected: %s", Messages.Error.USER_NOT_FOUND));

        ReflectionTestUtils.setField(eulaService, "EULA_STANDARD", "standard Eula URL");

        final User user = USER_NOT_FOUND;

        doReturn(user.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(user.getId());

        RequestException ex = assertThrows(RequestException.class, () -> eulaService.getEULA(mockedToken));
        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(USER_NOT_FOUND.getId());

        log.error("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", ex.getHttpStatus()));
        log.info(String.format("Returned from exception: %s", ex.getMessage()));
    }

    @Test
    public void whenGetEula_returnStandardEula() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s (standard)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        log.info("Expected: standard_EULA_url");

        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        log.info(String.format("Expected: %s", String.format(Messages.App.EULA_SELECT_ACTION, EVERY_USER)));

        ReflectionTestUtils.setField(eulaService, "EULA_STANDARD", "standard_EULA_url");

        final User userStandard = USER_STANDARD_EULA_NOT_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.getEULA(mockedToken);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        assertTrue(StringUtils.isNotBlank(((EulaDto) Objects.requireNonNull(responseBody).getData()).getUrl()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.App.EULA_SELECT_ACTION, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", ((EulaDto) responseBody.getData()).getUrl()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

        // managers

        log.info(String.format("### TESTING ### %s -> %s (managers)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        log.info(String.format("Expected: %s", "managers_EULA_url"));

        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        log.info(String.format("Expected: %s", String.format(Messages.App.EULA_SELECT_ACTION, MANAGERS)));

        ReflectionTestUtils.setField(eulaService, "EULA_MANAGERS", "managers_EULA_url");

        final User userManager = USER_MANAGER_EULA_STANDARD_NOT_ACCEPTED;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        response = eulaService.getEULA(mockedToken);
        assertTrue(Objects.nonNull(response));

        responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        assertTrue(StringUtils.isNotBlank(((EulaDto) Objects.requireNonNull(responseBody).getData()).getUrl()));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", ((EulaDto) responseBody.getData()).getUrl()));

    }

    @Test
    public void whenGetEula_returnManagerEula() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        log.info(String.format("Expected: %s", "managers_EULA_url"));

        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        log.info(String.format("Expected: %s", String.format(Messages.App.EULA_SELECT_ACTION, MANAGERS)));

        ReflectionTestUtils.setField(eulaService, "EULA_MANAGERS", "managers_EULA_url");

        final User userManager = USER_MANAGER_EULA_STANDARD_ACCEPTED_EULA_MANAGER_NOT;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        ResponseEntity<?> response = eulaService.getEULA(mockedToken);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertTrue(StringUtils.isNotBlank(((EulaDto) Objects.requireNonNull(responseBody).getData()).getUrl()));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", ((EulaDto) responseBody.getData()).getUrl()));

    }

    @Test
    public void whenGetEula_returnStandardEulaAlreadyAccepted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.CONFLICT));
        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ALREADY_ACCEPTED, EVERY_USER)));

        final User userStandard = USER_STANDARD_EULA_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.getEULA(mockedToken);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ALREADY_ACCEPTED, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenGetEula_returnManagerEulaAlreadyAccepted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.CONFLICT));
        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ALREADY_ACCEPTED, MANAGERS)));

        final User userManager = USER_MANAGER_EULA_STANDARD_AND_MANAGER_ACCEPTED;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        ResponseEntity<?> response = eulaService.getEULA(mockedToken);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ALREADY_ACCEPTED, MANAGERS));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", "response.getStatusCode()"));
        log.info(String.format("Returned: %s", "x"));

    }

    // putEula

    @Test
    public void whenUpdateEula_returnUserNotFound() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.NOT_FOUND));
        log.info(String.format("Expected: %s", Messages.Error.USER_NOT_FOUND));

        final User user = USER_NOT_FOUND;

        ReflectionTestUtils.setField(eulaService, "EULA_STANDARD", "standard Eula URL");
        doReturn(user.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.empty()).when(userRepository).findById(user.getId());

        RequestException ex = assertThrows(RequestException.class, () -> eulaService.putEULA(mockedToken, EEULAStatus.ACCEPT));
        assertEquals(ex.getHttpStatus(), HttpStatus.NOT_FOUND);
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findById(user.getId());

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", ex.getHttpStatus()));
        log.info(String.format("Returned: %s", ex.getMessage()));

    }

    @Test
    public void whenUpdateEulaAndUserIsStandard_returnEulaAlreadyAccepted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.CONFLICT));
        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ALREADY_ACCEPTED, EVERY_USER)));

        final User userStandard = USER_STANDARD_EULA_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.ACCEPT);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ALREADY_ACCEPTED, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenUpdateEulaAndUserIsManagerOrAdmin_returnEulaAlreadyAccepted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.CONFLICT));
        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ALREADY_ACCEPTED, MANAGERS)));

        final User userManager = USER_MANAGER_EULA_STANDARD_AND_MANAGER_ACCEPTED;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.ACCEPT);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ALREADY_ACCEPTED, MANAGERS));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenAcceptStandardEula_returnStandardEulaAccepted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s (standard)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ACCEPTED, EVERY_USER)));

        final User userStandard = USER_STANDARD_EULA_NOT_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.ACCEPT);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        verify(userRepository, atMostOnce()).save(userStandard);
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ACCEPTED, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

        log.info(String.format("### TESTING ### %s -> %s (managers)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ACCEPTED, EVERY_USER)));

        final User userManager = USER_MANAGER_EULA_STANDARD_NOT_ACCEPTED;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        response = eulaService.putEULA(mockedToken, EEULAStatus.ACCEPT);
        assertTrue(Objects.nonNull(response));

        responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ACCEPTED, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenAcceptManagerEula_returnManagerEulaAccepted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s (managers)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_ACCEPTED, MANAGERS)));

        final User userManager = USER_MANAGER_EULA_STANDARD_ACCEPTED_EULA_MANAGER_NOT;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.ACCEPT);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        verify(userRepository, atMostOnce()).save(userManager);
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_ACCEPTED, MANAGERS));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenPendingEula_returnStandardEulaPending() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s (standard)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_PENDING, EVERY_USER)));

        final User userStandard = USER_STANDARD_EULA_NOT_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.PENDING);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_PENDING, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenPendingEula_returnManagerEulaPending() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s (manager, and standard eula not accepted)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_PENDING, EVERY_USER)));

        final User userManager1 = USER_MANAGER_EULA_STANDARD_NOT_ACCEPTED;

        doReturn(userManager1.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager1)).when(userRepository).findById(userManager1.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.PENDING);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager1.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_PENDING, EVERY_USER));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

        log.info(String.format("### TESTING ### %s -> %s (manager, and manager eula not accepted)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_PENDING, MANAGERS)));

        final User userManager2 = USER_MANAGER_EULA_STANDARD_ACCEPTED_EULA_MANAGER_NOT;

        doReturn(userManager2.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager2)).when(userRepository).findById(userManager2.getId());

        response = eulaService.putEULA(mockedToken, EEULAStatus.PENDING);
        assertTrue(Objects.nonNull(response));

        responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager2.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_PENDING, MANAGERS));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenRejectStandardEula_returnUserDeleted() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s (standard)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String EVERY_USER = (String) ReflectionTestUtils.getField(eulaService, "EVERY_USER");
        String rejectionMessage = Messages.Info.EULA_DELETE_USER;
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_REJECTED, EVERY_USER, rejectionMessage)));

        final User userStandard = USER_STANDARD_EULA_NOT_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.REJECT);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        verify(userRepository, atMostOnce()).delete(userStandard);
        verify(userRepository, times(0)).save(userStandard);
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_REJECTED, EVERY_USER, rejectionMessage));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

        log.info(String.format("### TESTING ### %s -> %s (managers)", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_REJECTED, EVERY_USER, rejectionMessage)));

        final User userManager = USER_MANAGER_EULA_STANDARD_NOT_ACCEPTED;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());

        response = eulaService.putEULA(mockedToken, EEULAStatus.REJECT);
        assertTrue(Objects.nonNull(response));

        responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        verify(userRepository, atMostOnce()).delete(userManager);
        verify(userRepository, times(0)).save(userManager);
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_REJECTED, EVERY_USER, rejectionMessage));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenRejectManagerEula_returnUserDowngraded() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.OK));
        String MANAGERS = (String) ReflectionTestUtils.getField(eulaService, "MANAGERS");
        String rejectionMessage = Messages.Info.EULA_DOWNGRADE_USER;
        log.info(String.format("Expected: %s", String.format(Messages.Info.EULA_REJECTED, MANAGERS, rejectionMessage)));

        final User userManager = USER_MANAGER_EULA_STANDARD_ACCEPTED_EULA_MANAGER_NOT;

        doReturn(userManager.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userManager)).when(userRepository).findById(userManager.getId());
        doReturn(userManager).when(userRepository).save(userManager);

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.REJECT);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userManager.getId());
        verify(userRepository, times(0)).delete(userManager);
        verify(userRepository, atMostOnce()).save(userManager);
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(responseBody.getMessage(), String.format(Messages.Info.EULA_REJECTED, MANAGERS, rejectionMessage));

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

    @Test
    public void whenUpdateEula_returnActionInvalid() throws ParseException {
        log.info(String.format("### TESTING ### %s -> %s", this.getClass().getSimpleName(), new Object(){}.getClass().getEnclosingMethod().getName()));
        log.info(String.format("Expected: %s", HttpStatus.BAD_REQUEST));
        log.info(String.format("Expected: %s", Messages.Error.EULA_ACTION_INVALID));

        final User userStandard = USER_STANDARD_EULA_NOT_ACCEPTED;

        doReturn(userStandard.getId()).when(jwtUtils).getUserIdFromStringToken(mockedToken);
        doReturn(Optional.of(userStandard)).when(userRepository).findById(userStandard.getId());

        ResponseEntity<?> response = eulaService.putEULA(mockedToken, EEULAStatus.DELETE);
        assertTrue(Objects.nonNull(response));

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findById(userStandard.getId());
        assertTrue(StringUtils.isNotBlank(Objects.requireNonNull(responseBody).getMessage()));
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(responseBody.getMessage(), Messages.Error.EULA_ACTION_INVALID);

        log.info("### RESULTS ###");
        log.info(String.format("HTTP Returned Code: %s", response.getStatusCode()));
        log.info(String.format("Returned: %s", responseBody.getMessage()));

    }

}
