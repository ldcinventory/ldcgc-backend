package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.impl.AccountServiceImpl;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Email;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewStringToken;
import static org.ldcgc.backend.base.mock.MockedToken.generateNewToken;
import static org.ldcgc.backend.base.mock.MockedToken.generateRefreshToken;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getRandomMockedUserDto;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

@SpringBootTest
class AccountServiceImplTest {

    private AccountService accountService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    // repository
    @Mock private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    // email
    @Autowired private TemplateEngine templateEngine;
    @MockBean private JavaMailSender sender;

    private final PasswordEncoder passwordEncoder = defaultsForSpringSecurity_v5_8();
    private final PodamFactory factory = new PodamFactoryImpl();
    private String mockedToken;

    @BeforeEach
    public void init() {
        accountService = new AccountServiceImpl(authenticationManager, jwtUtils, userRepository, tokenRepository, passwordEncoder);
        mockedToken = generateNewStringToken(UserMapper.MAPPER.toEntity(getRandomMockedUserDto(ERole.ROLE_USER)));
    }

    // mocked users
    private final User USER_NOT_FOUND = User.builder().id(0).role(ERole.ROLE_USER).email("invalid@email.com").build();
    private final User USER_PASSWORD_DONT_MATCH = User.builder().id(0).role(ERole.ROLE_USER).email("test@test.com").password("test1").build();
    private final UserCredentialsDto USER_PASSWORD_DONT_MATCH_CR = UserCredentialsDto.builder().email("test@test.com").password("test2").build();
    private final User USER_NOT_EULA_STANDARD = User.builder().id(0).role(ERole.ROLE_USER).email("test@test.com").password("test").build();
    private final User USER_NOT_EULA_MANAGER = User.builder().id(0).role(ERole.ROLE_MANAGER).email("test@test.com").password("test").acceptedEULA(LocalDateTime.now()).build();
    private final User USER_STANDARD = User.builder().id(0).role(ERole.ROLE_USER).email("test@test.com").password("test").acceptedEULA(LocalDateTime.now()).build();

    // -> login
    @Test
    public void whenAuthenticateUser_returnUserNotFound() {
        final User user = USER_NOT_FOUND;
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_NOT_FOUND);

        doReturn(Optional.empty()).when(userRepository).findByEmail(user.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> accountService.login(userCredentials));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findByEmail(user.getEmail());
    }

    @Test
    public void whenAuthenticateUser_returnUserPasswordDontMatch() {
        final User user = USER_PASSWORD_DONT_MATCH;
        final UserCredentialsDto userCredentials = USER_PASSWORD_DONT_MATCH_CR;

        doReturn(Optional.of(user)).when(userRepository).findByEmail(userCredentials.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> accountService.login(userCredentials));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_PASSWORD_DONT_MATCH, ex.getMessage());

        verify(userRepository, atMostOnce()).findByEmail(user.getEmail());
    }

    @Test
    public void whenAuthenticateUser_returnStandardEulaNotAccepted() throws ParseException, JOSEException {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_NOT_EULA_STANDARD);
        String encodedPassword = encodePassword(USER_NOT_EULA_STANDARD.getPassword());
        final User user = USER_NOT_EULA_STANDARD.toBuilder().password(encodedPassword).build();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(userCredentials.getEmail());
        doReturn(generateNewToken(user)).when(jwtUtils).generateNewToken(user);
        doReturn(generateRefreshToken(user)).when(jwtUtils).generateRefreshToken(user);

        ResponseEntity<?> response = accountService.login(userCredentials);
        assertNotNull(response);

        Response.DTOWithLocation responseBody = (Response.DTOWithLocation) response.getBody();
        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(Messages.Error.EULA_STANDARD_NOT_ACCEPTED, Objects.requireNonNull(responseBody).getMessage());
        assertEquals(Messages.App.EULA_ENDPOINT, responseBody.getLocation());
        assertNotNull(response.getHeaders().get("x-header-payload-token"));
        assertNotNull(response.getHeaders().get("x-signature-token"));

    }

    @Test
    public void whenAuthenticateUser_returnManagerEulaNotAccepted() throws ParseException, JOSEException {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_NOT_EULA_MANAGER);
        String encodedPassword = encodePassword(USER_NOT_EULA_MANAGER.getPassword());
        final User user = USER_NOT_EULA_MANAGER.toBuilder().password(encodedPassword).build();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(userCredentials.getEmail());
        doReturn(generateNewToken(user)).when(jwtUtils).generateNewToken(user);
        doReturn(generateRefreshToken(user)).when(jwtUtils).generateRefreshToken(user);

        ResponseEntity<?> response = accountService.login(userCredentials);
        assertNotNull(response);

        Response.DTOWithLocation responseBody = (Response.DTOWithLocation) response.getBody();
        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(Messages.Error.EULA_MANAGER_NOT_ACCEPTED, Objects.requireNonNull(responseBody).getMessage());
        assertEquals(Messages.App.EULA_ENDPOINT, responseBody.getLocation());
        assertNotNull(response.getHeaders().get("x-header-payload-token"));
        assertNotNull(response.getHeaders().get("x-signature-token"));

    }

    @Test
    public void whenAuthenticateUser_returnUserDetails() throws ParseException, JOSEException {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_STANDARD);
        String encodedPassword = encodePassword(USER_STANDARD.getPassword());
        final User user = USER_STANDARD.toBuilder().password(encodedPassword).build();

        doReturn(Optional.of(user)).when(userRepository).findByEmail(userCredentials.getEmail());
        doReturn(generateNewToken(user)).when(jwtUtils).generateNewToken(user);
        doReturn(generateRefreshToken(user)).when(jwtUtils).generateRefreshToken(user);

        ResponseEntity<?> response = accountService.login(userCredentials);
        assertNotNull(response);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Objects.requireNonNull(responseBody).getData().getClass(), UserDto.class);
        assertNotNull(response.getHeaders().get("x-header-payload-token"));
        assertNotNull(response.getHeaders().get("x-signature-token"));
    }

    // -> logout
    @Test
    public void whenLogoutUser_returnOK() throws ParseException, JOSEException {
        String encodedPassword = encodePassword(USER_STANDARD.getPassword());
        final User user = USER_STANDARD.toBuilder().password(encodedPassword).build();
        SignedJWT mockedSignedToken = generateNewToken(user);
        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(1).when(jwtUtils).getUserIdFromJwtToken(mockedSignedToken);

        ResponseEntity<?> response = accountService.logout(mockedToken);
        assertNotNull(response);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Messages.Info.LOGOUT_SUCCESSFUL, Objects.requireNonNull(responseBody).getMessage());
        assertTrue(Objects.isNull(response.getHeaders().get("x-header-payload-token")));
        assertTrue(Objects.isNull(response.getHeaders().get("x-signature-token")));

    }

    // -> recover
    @Test
    public void whenRecoverCredentials_returnUserNotFound() {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_NOT_FOUND);

        doReturn(Optional.empty()).when(userRepository).findByEmail(userCredentials.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> accountService.recoverCredentials(userCredentials));
        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());
    }

    @Test
    public void whenRecoverCredentials_returnEmailSent() throws ParseException, JOSEException {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_STANDARD);
        final User user = USER_STANDARD;

        doReturn(Optional.of(user)).when(userRepository).findByEmail(userCredentials.getEmail());
        doReturn(generateNewToken(user)).when(jwtUtils).generateNewRecoveryToken(user);

        Email email = new Email(templateEngine, sender);
        Email.setINSTANCE(email);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        doReturn(mimeMessage).when(sender).createMimeMessage();

        given(Email.sendRecoveringCredentials(user.getEmail(), mockedToken)).willAnswer(
            invocation -> ResponseEntity.status(HttpStatus.CREATED).body(Messages.Info.CREDENTIALS_EMAIL_SENT));

        ResponseEntity<?> response = accountService.recoverCredentials(userCredentials);
        assertNotNull(response);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Messages.Info.CREDENTIALS_EMAIL_SENT, Objects.requireNonNull(responseBody).getMessage());

        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());

    }

    // -> validate token
    @Test
    public void whenValidatingToken_returnRecoveryTokenNotValidNotFound() throws ParseException, JOSEException {
        SignedJWT mockedSignedToken = generateNewToken(USER_STANDARD);
        Token mockedTokenEntity = factory.manufacturePojo(Token.class);
        mockedTokenEntity.setRecoveryToken(false);

        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(Optional.empty()).when(tokenRepository).findByJwtID(mockedSignedToken.getHeader().getKeyID());

        RequestException ex = assertThrows(RequestException.class, () -> accountService.validateToken(mockedToken));
        assertNotNull(ex);

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_FOUND, ex.getMessage());

        verify(tokenRepository, atMostOnce()).findByJwtID(mockedSignedToken.getHeader().getKeyID());

    }

    @Test
    public void whenValidatingToken_returnJWTNotForRecovery() throws ParseException, JOSEException {
        SignedJWT mockedSignedToken = generateNewToken(USER_STANDARD);
        Token mockedTokenEntity = factory.manufacturePojo(Token.class);
        mockedTokenEntity.setRecoveryToken(false);
        mockedTokenEntity.setIssuedAt(LocalDateTime.now());
        mockedTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(1));

        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(Optional.of(mockedTokenEntity)).when(tokenRepository).findByJwtID(mockedSignedToken.getHeader().getKeyID());

        RequestException ex = assertThrows(RequestException.class, () -> accountService.validateToken(mockedToken));
        assertNotNull(ex);

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.JWT_NOT_FOR_RECOVERY_REFRESH, ex.getMessage());

        verify(tokenRepository, atMostOnce()).findByJwtID(mockedSignedToken.getHeader().getKeyID());
    }

    @Test
    public void whenValidatingToken_returnUserNotFoundToken() throws ParseException, JOSEException {
        SignedJWT mockedSignedToken = generateNewToken(USER_STANDARD);
        Token mockedTokenEntity = factory.manufacturePojo(Token.class);
        mockedTokenEntity.setRecoveryToken(true);
        mockedTokenEntity.setIssuedAt(LocalDateTime.now());
        mockedTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(1));

        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(Optional.of(mockedTokenEntity)).when(tokenRepository).findByJwtID(mockedSignedToken.getHeader().getKeyID());

        Integer userIdFromTokenString = mockedTokenEntity.getUserId();
        doReturn(userIdFromTokenString).when(jwtUtils).getUserIdFromJwtToken(mockedSignedToken);
        doReturn(Optional.empty()).when(userRepository).findById(userIdFromTokenString);

        RequestException ex = assertThrows(RequestException.class, () -> accountService.validateToken(mockedToken));
        assertNotNull(ex);

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND_TOKEN, ex.getMessage());

        verify(tokenRepository, atMostOnce()).findByJwtID(mockedSignedToken.getHeader().getKeyID());
        verify(userRepository, atMostOnce()).findById(userIdFromTokenString);

    }

    @Test
    public void whenValidatingToken_returnTokenExpired() throws ParseException, JOSEException {
        SignedJWT mockedSignedToken = generateNewToken(USER_STANDARD);
        Token mockedTokenEntity = factory.manufacturePojo(Token.class);
        mockedTokenEntity.setRecoveryToken(true);
        mockedTokenEntity.setIssuedAt(LocalDateTime.now().minusDays(1).minusMinutes(1));
        mockedTokenEntity.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(Optional.of(mockedTokenEntity)).when(tokenRepository).findByJwtID(mockedSignedToken.getHeader().getKeyID());

        Integer userIdFromTokenString = mockedTokenEntity.getUserId();
        doReturn(userIdFromTokenString).when(jwtUtils).getUserIdFromJwtToken(mockedSignedToken);
        doReturn(Optional.empty()).when(userRepository).findById(userIdFromTokenString);

        RequestException ex = assertThrows(RequestException.class, () -> accountService.validateToken(mockedToken));
        assertNotNull(ex);

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_EXPIRED, ex.getMessage());

        verify(tokenRepository, atMostOnce()).findByJwtID(mockedSignedToken.getHeader().getKeyID());
        verify(userRepository, atMostOnce()).findById(userIdFromTokenString);

    }

    @Test
    public void whenValidatingToken_returnRecoveryTokenValid() throws ParseException, JOSEException {
        SignedJWT mockedSignedToken = generateNewToken(USER_STANDARD);
        Token mockedTokenEntity = factory.manufacturePojo(Token.class);
        mockedTokenEntity.setRecoveryToken(true);
        mockedTokenEntity.setIssuedAt(LocalDateTime.now());
        mockedTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(1));

        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(Optional.of(mockedTokenEntity)).when(tokenRepository).findByJwtID(mockedSignedToken.getHeader().getKeyID());

        Integer userIdFromTokenString = mockedTokenEntity.getUserId();
        doReturn(userIdFromTokenString).when(jwtUtils).getUserIdFromJwtToken(mockedSignedToken);
        doReturn(Optional.of(USER_STANDARD)).when(userRepository).findById(userIdFromTokenString);

        ResponseEntity<?> response = accountService.validateToken(mockedToken);
        assertNotNull(response);

        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Messages.Info.RECOVERY_TOKEN_VALID, Objects.requireNonNull(responseBody).getMessage());

        verify(tokenRepository, atMostOnce()).findByJwtID(mockedSignedToken.getHeader().getKeyID());
        verify(userRepository, atMostOnce()).findById(userIdFromTokenString);
    }

    // -> new credentials
    @Test
    public void whenSettingNewCredentials_returnUserNotFound() throws ParseException {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_STANDARD)
            .toBuilder().token(mockedToken).build();

        userCredentials.toBuilder().token(mockedToken).build();

        // not a mocked service, let's make it by spying it :)
        accountService = Mockito.spy(accountService);

        doReturn(ResponseEntity.status(HttpStatus.OK).body(Messages.Info.RECOVERY_TOKEN_VALID)).when(accountService).validateToken(userCredentials.getToken());

        doReturn(Optional.empty()).when(userRepository).findByEmail(userCredentials.getEmail());

        RequestException ex = assertThrows(RequestException.class, () -> accountService.newCredentials(userCredentials));
        assertNotNull(ex);

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(Messages.Error.USER_NOT_FOUND, ex.getMessage());

        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());
        // verify since it's not calling validateToken
        verify(jwtUtils, times(0)).getDecodedJwt(Mockito.anyString());
        verify(tokenRepository, times(0)).findByJwtID(Mockito.anyString());
        verify(jwtUtils, times(0)).getUserIdFromJwtToken(Mockito.any(SignedJWT.class));
        verify(userRepository, times(0)).findById(Mockito.anyInt());
    }

    @Test
    public void whenSettingNewCredentials_returnCredentialsUpdated() throws ParseException {
        final UserCredentialsDto userCredentials = UserMapper.MAPPER.toCredentialsDTO(USER_STANDARD)
            .toBuilder().token(mockedToken).build();

        userCredentials.toBuilder().token(mockedToken).build();

        // not a mocked service, let's make it by spying it :)
        accountService = Mockito.spy(accountService);

        doReturn(ResponseEntity.status(HttpStatus.OK).body(Messages.Info.RECOVERY_TOKEN_VALID)).when(accountService).validateToken(userCredentials.getToken());

        doReturn(Optional.of(USER_STANDARD)).when(userRepository).findByEmail(userCredentials.getEmail());

        ResponseEntity<?> response = accountService.newCredentials(userCredentials);
        assertNotNull(response);

        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Messages.Info.USER_CREDENTIALS_UPDATED, Objects.requireNonNull(responseBody).getMessage());

        verify(userRepository, atMostOnce()).findByEmail(userCredentials.getEmail());
        // verify since it's not calling validateToken
        verify(jwtUtils, times(0)).getDecodedJwt(Mockito.anyString());
        verify(tokenRepository, times(0)).findByJwtID(Mockito.anyString());
        verify(jwtUtils, times(0)).getUserIdFromJwtToken(Mockito.any(SignedJWT.class));
        verify(userRepository, times(0)).findById(Mockito.anyInt());

    }

    @Test
    public void whenRefreshingToken_returnRefreshToken() throws ParseException, JOSEException {
        SignedJWT mockedSignedToken = generateNewToken(USER_STANDARD);
        Token mockedTokenEntity = factory.manufacturePojo(Token.class);
        mockedTokenEntity.setRefreshToken(true);
        mockedTokenEntity.setIssuedAt(LocalDateTime.now());
        mockedTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(1));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        doReturn(mockedSignedToken).when(jwtUtils).getDecodedJwt(mockedToken);
        doReturn(Optional.of(mockedTokenEntity)).when(tokenRepository).findByJwtID(mockedSignedToken.getHeader().getKeyID());

        Integer userIdFromTokenString = mockedTokenEntity.getUserId();
        doReturn(userIdFromTokenString).when(jwtUtils).getUserIdFromJwtToken(mockedSignedToken);
        doReturn(Optional.of(USER_STANDARD)).when(userRepository).findById(userIdFromTokenString);
        doReturn(mockedSignedToken).when(jwtUtils).generateNewToken(USER_STANDARD);

        ResponseEntity<?> response = accountService.refreshToken(httpRequest, httpResponse, mockedToken);
        assertNotNull(response);

        Response.DTO responseBody = (Response.DTO) response.getBody();
        assertNotNull(responseBody);
        UserDto userDto = (UserDto) responseBody.getData();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(Messages.Info.TOKEN_REFRESHED, Objects.requireNonNull(responseBody).getMessage());
        assertNotNull(httpResponse.getHeader("x-header-payload-token"));
        assertNotNull(httpResponse.getHeader("x-signature-token"));
        assertNotNull(httpResponse.getHeader("x-refresh-token"));
        assertNotNull(userDto);
        assertNotNull(userDto.getTokenExpires());
        assertNotNull(userDto.getRefreshExpires());

        verify(tokenRepository, atMostOnce()).findByJwtID(mockedSignedToken.getHeader().getKeyID());
        verify(userRepository, atMostOnce()).findById(userIdFromTokenString);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
