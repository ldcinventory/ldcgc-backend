package org.ldcgc.backend.security.jwt;

import com.nimbusds.jose.JOSEException;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.base.mock.MockedToken;
import org.ldcgc.backend.base.mock.MockedUserVolunteer;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.payload.dto.users.TokenDto;
import org.ldcgc.backend.payload.mapper.users.TokenMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
public class JwtUtilsTests {

    @Mock private JwtUtils jwtUtils;
    @Mock private TokenRepository tokenRepository;

    @BeforeEach
    public void init() {
        jwtUtils = new JwtUtils(tokenRepository);
    }

    @Test
    public void generateNewBothTokens() throws ParseException, JOSEException {
        User user = MockedUserVolunteer.getRandomMockedUser();
        Token refreshToken = MockedToken.generateNewToken(user, false, true);
        Token token = MockedToken.generateNewToken(user, false, false);

        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationSeconds", 86400);

        // refresh
        ReflectionTestUtils.setField(jwtUtils, "isRefreshToken", true);
        doReturn(refreshToken).when(tokenRepository).saveAndFlush(any(Token.class));
        assertNotNull(jwtUtils.generateNewToken(user));

        // regular
        ReflectionTestUtils.setField(jwtUtils, "isRefreshToken", false);
        doReturn(token).when(tokenRepository).saveAndFlush(any(Token.class));
        assertNotNull(jwtUtils.generateNewToken(user));
    }

    @Test
    public void generateNewToken() throws ParseException, JOSEException {
        User user = MockedUserVolunteer.getRandomMockedUser();
        Token token = MockedToken.generateNewToken(user, false, false);

        BidiMap<Integer, TokenDto> refreshTokenRepo = new DualHashBidiMap<>();
        refreshTokenRepo.put(user.getId(), TokenMapper.MAPPER.toDto(token));
        ReflectionTestUtils.setField(jwtUtils, "refreshTokenLocalRepository", refreshTokenRepo);

        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationSeconds", 86400);

        // regular
        ReflectionTestUtils.setField(jwtUtils, "isRefreshToken", false);
        doReturn(token).when(tokenRepository).saveAndFlush(any(Token.class));
        assertNotNull(jwtUtils.generateNewToken(user));
    }

    @Test
    public void generateNewRefreshToken() throws ParseException, JOSEException {
        User user = MockedUserVolunteer.getRandomMockedUser();
        Token refreshToken = MockedToken.generateNewToken(user, false, true);

        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationSeconds", 86400);

        // refresh
        ReflectionTestUtils.setField(jwtUtils, "isRefreshToken", true);
        doReturn(refreshToken).when(tokenRepository).saveAndFlush(any(Token.class));
        assertNotNull(jwtUtils.generateNewToken(user));
    }

    @Test
    public void generateNewRecoveryToken() {
        fail(NOT_YET_IMPLEMENTED);

    }

    @Test
    public void getEmailFromJwtToken() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void getUserIdFromJwtToken() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void getUserIdFromStringToken() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void getDecodedJwt() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void verifyJwt() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void verifyJwtLocalRepoEmpty() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void verifyJwtRefreshLocalRepoEmpty() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void verifyJwtLocalToken() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void verifyJwtLocalRefreshToken() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void cleanLocalTokensFromUserId() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    public void getBySignedJwtFromLocal() {
        fail(NOT_YET_IMPLEMENTED);
    }
}
