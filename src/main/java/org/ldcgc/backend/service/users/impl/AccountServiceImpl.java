package org.ldcgc.backend.service.users.impl;

import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.TokenDto;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.mapper.users.TokenMapper;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.ldcgc.backend.security.jwt.JwtUtils.cleanLocalTokensFromUserId;
import static org.ldcgc.backend.security.jwt.JwtUtils.getBySignedJwtFromLocal;
import static org.ldcgc.backend.util.common.ERole.ROLE_ADMIN;
import static org.ldcgc.backend.util.common.ERole.ROLE_MANAGER;
import static org.ldcgc.backend.util.conversion.Convert.dateToLocalDateTime;
import static org.ldcgc.backend.util.creation.Email.sendRecoveringCredentials;

@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private User petitionUser = null;

    public ResponseEntity<?> login(UserCredentialsDto userCredentials) throws ParseException, JOSEException {

        User userEntity = userRepository.findByEmail(userCredentials.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userCredentials.getPassword(), userEntity.getPassword()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.USER_PASSWORD_DONT_MATCH);

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userEntity.getRole().name());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userEntity.getEmail(), userCredentials.getPassword(), Collections.singletonList(grantedAuthority));
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpHeaders headers = new HttpHeaders();

        SignedJWT refreshToken = jwtUtils.generateRefreshToken(userEntity);
        headers.add("x-refresh-token", refreshToken.getParsedString());

        SignedJWT jwt = jwtUtils.generateNewToken(userEntity);
        headers.add("x-header-payload-token", String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1]));
        headers.add("x-signature-token", jwt.getParsedParts()[2].toString());

        // HttpServletRequest actualRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // get eula details (standard user)
        if (userEntity.getAcceptedEULA() == null)
            return Constructor.buildResponseObjectLocation(HttpStatus.FORBIDDEN, Messages.Error.EULA_STANDARD_NOT_ACCEPTED, Messages.App.EULA_ENDPOINT, headers);

        // get eula details (manager)
        if((userEntity.getRole().equalsAny(ROLE_MANAGER, ROLE_ADMIN))
            && userEntity.getAcceptedEULAManager() == null)
            return Constructor.buildResponseObjectLocation(HttpStatus.FORBIDDEN, Messages.Error.EULA_MANAGER_NOT_ACCEPTED, Messages.App.EULA_ENDPOINT, headers);

        UserDto userDto = UserMapper.MAPPER.toDTO(userEntity).toBuilder()
            .tokenExpires(dateToLocalDateTime(jwt.getJWTClaimsSet().getExpirationTime()))
            .refreshExpires(dateToLocalDateTime(refreshToken.getJWTClaimsSet().getExpirationTime()))
            .build();

        return Constructor.buildResponseObjectHeader(HttpStatus.OK, userDto, headers);
    }

    public ResponseEntity<?> logout(String token) throws ParseException {
        Integer userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getDecodedJwt(token));

        cleanLocalTokensFromUserId(userId, true);
        tokenRepository.deleteAllTokensFromUser(userId);

        SecurityContextHolder.clearContext();

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.LOGOUT_SUCCESSFUL);
    }

    public ResponseEntity<?> recoverCredentials(UserCredentialsDto userCredentials) throws ParseException, JOSEException {
        User user = userRepository.findByEmail(userCredentials.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        SignedJWT jwt = jwtUtils.generateNewRecoveryToken(user);

        return sendRecoveringCredentials(userCredentials.getEmail(), jwt.getParsedString());
    }

    public ResponseEntity<?> validateToken(String token) throws ParseException {
        SignedJWT jwt = jwtUtils.getDecodedJwt(token);

        // check exists
        boolean isRefreshToken = "true".equals(((Map<?, ?>) jwt.getJWTClaimsSet().getClaim("userClaims")).get("refresh-token"));
        TokenDto tokenDto = getBySignedJwtFromLocal(jwt, isRefreshToken);

        // check token exists
        if(tokenDto == null)
            tokenDto= TokenMapper.MAPPER.toDto(tokenRepository.findByJwtID(jwt.getHeader().getKeyID()).orElseThrow(() ->
                new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_NOT_FOUND)));

        if(tokenDto.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.TOKEN_EXPIRED);

        // check recovery & refresh token
        if (!tokenDto.isRecoveryToken() && !tokenDto.isRefreshToken())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.JWT_NOT_FOR_RECOVERY_REFRESH);

        Integer userIdFromTokenEntity = tokenDto.getUserId();
        Integer userIdFromTokenString = jwtUtils.getUserIdFromJwtToken(jwt);

        Preconditions.checkArgument(userIdFromTokenEntity.equals(userIdFromTokenString));

        // check user exists
        petitionUser = userRepository.findById(userIdFromTokenEntity).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND_TOKEN));

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.RECOVERY_TOKEN_VALID);
    }

    public ResponseEntity<?> newCredentials(UserCredentialsDto userCredentials) throws ParseException {
        String recoveryToken = userCredentials.getToken();

        validateToken(recoveryToken);

        // get uset details
        User user = userRepository.findByEmail(userCredentials.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        userRepository.saveAndFlush(user);
        tokenRepository.deleteRecoveryTokenForUserId(user.getId());

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.USER_CREDENTIALS_UPDATED);
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws ParseException, JOSEException {
        validateToken(refreshToken);

        tokenRepository.deleteNonRefreshTokensFromUser(petitionUser.getId());

        SignedJWT jwt = jwtUtils.generateNewToken(petitionUser);
        SignedJWT refreshJwt = jwtUtils.getDecodedJwt(refreshToken);

        response.setHeader("x-header-payload-token", String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1]));
        response.setHeader("x-signature-token", jwt.getParsedParts()[2].toString());
        response.setHeader("x-refresh-token", refreshJwt.getParsedString());

        UserDto userDto = UserDto.builder()
            .tokenExpires(dateToLocalDateTime(jwt.getJWTClaimsSet().getExpirationTime()))
            .refreshExpires(dateToLocalDateTime(refreshJwt.getJWTClaimsSet().getExpirationTime())).build();

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.TOKEN_REFRESHED, userDto);
    }

}
