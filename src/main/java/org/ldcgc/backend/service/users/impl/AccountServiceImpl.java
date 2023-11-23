package org.ldcgc.backend.service.users.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.users.User;
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

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.USER_PASSWORD_DOESNT_MATCH;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(UserDto user) throws ParseException, JOSEException {

        User userEntity = userRepository.findByEmail(user.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(USER_NOT_FOUND)));

        if (!passwordEncoder.matches(user.getPassword(), userEntity.getPassword()))
            throw new RequestException(HttpStatus.BAD_REQUEST, getErrorMessage(USER_PASSWORD_DOESNT_MATCH));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userEntity.getRole().name());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userEntity.getId(), user.getPassword(), Collections.singletonList(grantedAuthority));
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

    public ResponseEntity<?> logout(String token) {
        return null;
    }

    public ResponseEntity<?> recoverCredentials(UserCredentialsDto userCredentials) {
        return null;
    }

    public ResponseEntity<?> validateToken(String token) {
        return null;
    }

    public ResponseEntity<?> newCredentials(UserCredentialsDto userCredentials) {
        return null;
    }
}
