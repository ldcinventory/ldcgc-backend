package org.ldcgc.backend.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.ServletException;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.base.mock.MockedToken;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.security.user.UserDetailsImpl;
import org.ldcgc.backend.security.user.UserDetailsServiceImpl;
import org.ldcgc.backend.util.common.ERole;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.ldcgc.backend.base.mock.MockedToken.getHeaderPayloadFromToken;
import static org.ldcgc.backend.base.mock.MockedToken.getSignatureFromToken;
import static org.ldcgc.backend.base.mock.MockedUserVolunteer.getMockedUserFromMockedUserDetailsImpl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@SpringBootTest
@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
public class AuthTokenFilterTests {

    private AuthTokenFilter authTokenFilter;

    @Mock private UserDetailsServiceImpl userDetailsService;
    @Mock private JwtUtils jwtUtils;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void init() {
        authTokenFilter = new AuthTokenFilter(userDetailsService, jwtUtils);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

    }

    @Test
    public void doFilterInternalNormal_returnOK() throws ServletException, IOException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/accounts/refresh-token", true);

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doReturn(true).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());
        doReturn(new Faker().internet().emailAddress()).when(jwtUtils).getEmailFromJwtToken(any(SignedJWT.class));
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(anyString());

        authTokenFilter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void doFilterInternalRefresh_returnOK() throws ServletException, IOException, IllegalAccessException, ParseException, JOSEException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        SignedJWT signedRefreshJWT = MockedToken.generateSignedRefreshToken(user);
        request.addHeader("x-refresh-token", signedRefreshJWT.getParsedString());
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/accounts/refresh-token", true);

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doReturn(true).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());
        doReturn(new Faker().internet().emailAddress()).when(jwtUtils).getEmailFromJwtToken(any(SignedJWT.class));
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(anyString());

        authTokenFilter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void doFilterInternalAuthIsNotPresent_returnOK() throws ServletException, IOException, IllegalAccessException {

        FieldUtils.writeField(request, "method", "GET", true);
        FieldUtils.writeField(request, "requestURI", "/api/alive", true);

        authTokenFilter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void doFilterInternalNonTokenEndpoint_returnOK() throws ServletException, IOException, IllegalAccessException, ParseException, JOSEException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        SignedJWT signedRefreshJWT = MockedToken.generateSignedRefreshToken(user);
        request.addHeader("x-refresh-token", signedRefreshJWT.getParsedString());
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        FieldUtils.writeField(request, "method", "GET", true);
        FieldUtils.writeField(request, "requestURI", "/api/alive", true);

        authTokenFilter.doFilterInternal(request, response, filterChain);

    }

    @Test
    public void doFilterInternal_returnTokenNotValid() throws RequestException, IllegalAccessException {
        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/accounts/logout", true);

        request.addHeader("x-header-payload-token", RandomStringUtils.randomAlphanumeric(10));
        request.addHeader("x-signature-token", RandomStringUtils.randomAlphanumeric(10));

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_VALID, ex.getMessage());

    }

    @Test
    public void doFilterInternalNotExemptedEndpoint_returnStandardEulaNotAccepted() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        SignedJWT signedRefreshJWT = MockedToken.generateSignedRefreshToken(user);
        request.addHeader("x-refresh-token", signedRefreshJWT.getParsedString());
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doReturn(true).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());
        doReturn(new Faker().internet().emailAddress()).when(jwtUtils).getEmailFromJwtToken(any(SignedJWT.class));
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(anyString());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.EULA_STANDARD_NOT_ACCEPTED, ex.getMessage());

    }

    @Test
    public void doFilterInternalNotExemptedEndpoint_returnManagerEulaNotAccepted() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_MANAGER, true, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        SignedJWT signedRefreshJWT = MockedToken.generateSignedRefreshToken(user);
        request.addHeader("x-refresh-token", signedRefreshJWT.getParsedString());
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doReturn(true).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());
        doReturn(new Faker().internet().emailAddress()).when(jwtUtils).getEmailFromJwtToken(any(SignedJWT.class));
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(anyString());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.EULA_MANAGER_NOT_ACCEPTED, ex.getMessage());

    }

    @Test
    public void doFilterInternalNotExemptedEndpoint_returnAdminEulaNotAccepted() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        SignedJWT signedRefreshJWT = MockedToken.generateSignedRefreshToken(user);
        request.addHeader("x-refresh-token", signedRefreshJWT.getParsedString());
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doReturn(true).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());
        doReturn(new Faker().internet().emailAddress()).when(jwtUtils).getEmailFromJwtToken(any(SignedJWT.class));
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(anyString());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(Messages.Error.EULA_MANAGER_NOT_ACCEPTED, ex.getMessage());

    }

    @Test
    public void doFilterInternal_returnParseException() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doThrow(ParseException.class).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_VALID, ex.getMessage());

    }

    @Test
    public void doFilterInternal_returnJOSEException() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doThrow(JOSEException.class).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_VALID, ex.getMessage());

    }

    @Test
    public void doFilterInternal_returnIllegalArgumentException() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doThrow(IllegalArgumentException.class).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_VALID, ex.getMessage());

    }

    @Test
    public void doFilterInternal_returnNullPointerException() throws RequestException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true, false);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = MockedToken.generateSignedToken(user);
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doThrow(NullPointerException.class).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/volunteers", true);

        RequestException ex = assertThrows(RequestException.class, () -> authTokenFilter.doFilterInternal(request, response, filterChain));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.TOKEN_NOT_VALID, ex.getMessage());

    }

    @Test
    public void doFilterInternalSetExpirationTimeFromLocalDateTime_returnOK() throws ServletException, IOException, ParseException, JOSEException, IllegalAccessException {
        UserDetailsImpl userDetails = MockedUserVolunteer.getMockedUserDetailsImpl(ERole.ROLE_ADMIN, true);

        User user = getMockedUserFromMockedUserDetailsImpl(userDetails);

        SignedJWT signedJWT = spy(MockedToken.generateSignedToken(user));
        SignedJWT signedRefreshJWT = MockedToken.generateSignedRefreshToken(user);
        request.addHeader("x-refresh-token", signedRefreshJWT.getParsedString());
        request.addHeader("x-header-payload-token", getHeaderPayloadFromToken(signedJWT));
        request.addHeader("x-signature-token", getSignatureFromToken(signedJWT));

        FieldUtils.writeField(request, "method", "POST", true);
        FieldUtils.writeField(request, "requestURI", "/api/accounts/refresh-token", true);

        doReturn(signedJWT).when(jwtUtils).getDecodedJwt(anyString());
        doReturn(true).when(jwtUtils).verifyJwt(any(SignedJWT.class), isNull());
        doReturn(new Faker().internet().emailAddress()).when(jwtUtils).getEmailFromJwtToken(any(SignedJWT.class));
        doReturn(userDetails).when(userDetailsService).loadUserByUsername(anyString());
        doThrow(new ParseException("Error", 0)).when(signedJWT).getJWTClaimsSet();

        authTokenFilter.doFilterInternal(request, response, filterChain);

    }

}
