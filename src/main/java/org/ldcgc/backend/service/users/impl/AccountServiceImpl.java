package org.ldcgc.backend.service.users.impl;

import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AccountService;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.ldcgc.backend.util.creation.Email.sendRecoveringCredentials;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.JWT_NOT_FOR_RECOVERY;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.RECOVERY_TOKEN_NOT_VALID_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_PASSWORD_DOESNT_MATCH;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.LOGOUT_SUCCESSFUL;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.RECOVERY_TOKEN_VALID;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.USER_CREDENTIALS_UPDATED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(UserDto user) throws ParseException, JOSEException {

        User userEntity = userRepository.findByEmail(user.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        if (!passwordEncoder.matches(user.getPassword(), userEntity.getPassword()))
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(USER_PASSWORD_DOESNT_MATCH));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userEntity.getRole().name());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userEntity.getEmail(), user.getPassword(), Collections.singletonList(grantedAuthority));
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        SignedJWT jwt = jwtUtils.generateNewToken(userEntity);

        HttpHeaders headers = new HttpHeaders();

        LocalDateTime now = LocalDateTime.now();

        headers.add("x-header-payload-token", String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1]));
        headers.add("x-signature-token", jwt.getParsedParts()[2].toString());
        headers.set("Expires", now.plus(Duration.ofMinutes(10)).toString());

        return Constructor.buildResponseObjectHeader(HttpStatus.OK, UserMapper.MAPPER.toDTO(userEntity), headers);
    }

    public ResponseEntity<?> logout(String token) throws ParseException {
        Integer userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getDecodedJwt(token));
        tokenRepository.deleteAllTokensFromUser(userId);

        SecurityContextHolder.clearContext();

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(LOGOUT_SUCCESSFUL));
    }

    public ResponseEntity<?> recoverCredentials(UserCredentialsDto userCredentials) throws ParseException, JOSEException {
        User user = userRepository.findByEmail(userCredentials.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        jwtUtils.setIsRecoveryToken(true);
        SignedJWT jwt = jwtUtils.generateNewToken(user);
        jwtUtils.setIsRecoveryToken(false); // reset recovery token state

        return sendRecoveringCredentials(userCredentials.getEmail(), jwt.getParsedString());
    }

    public ResponseEntity<?> validateToken(String token) throws ParseException {
        SignedJWT jwt = jwtUtils.getDecodedJwt(token);

        // check exists
        Token tokenEntity = tokenRepository.findByJwtID(jwt.getHeader().getKeyID()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(RECOVERY_TOKEN_NOT_VALID_NOT_FOUND)));

        // check is recovery token
        if (!tokenEntity.isRecoveryToken())
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(JWT_NOT_FOR_RECOVERY));

        Integer userIdFromTokenEntity = tokenEntity.getUserId();
        Integer userIdFromTokenString = jwtUtils.getUserIdFromJwtToken(jwt);

        Preconditions.checkArgument(userIdFromTokenEntity.equals(userIdFromTokenString));

        // check user exists
        userRepository.findById(userIdFromTokenEntity).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(RECOVERY_TOKEN_VALID));
    }

    public ResponseEntity<?> newCredentials(UserCredentialsDto userCredentials) throws ParseException {
        validateToken(userCredentials.getToken());

        // get uset details
        User user = userRepository.findByEmail(userCredentials.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, getErrorMessage(USER_NOT_FOUND)));

        user.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        userRepository.saveAndFlush(user);

        return Constructor.buildResponseMessage(HttpStatus.OK, getInfoMessage(USER_CREDENTIALS_UPDATED));
    }

}
