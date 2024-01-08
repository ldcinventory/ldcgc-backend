package org.ldcgc.backend.service.users.impl;

import com.google.common.base.Preconditions;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.users.Token;
import org.ldcgc.backend.db.model.users.User;
import org.ldcgc.backend.db.repository.users.TokenRepository;
import org.ldcgc.backend.db.repository.users.UserRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.mapper.users.UserMapper;
import org.ldcgc.backend.security.jwt.JwtUtils;
import org.ldcgc.backend.service.users.AccountService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.ParseException;
import java.util.Collections;
import java.util.Objects;

import static java.lang.Boolean.FALSE;
import static org.ldcgc.backend.util.common.ERole.ROLE_ADMIN;
import static org.ldcgc.backend.util.common.ERole.ROLE_MANAGER;
import static org.ldcgc.backend.util.creation.Email.sendRecoveringCredentials;

@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

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

        SignedJWT jwt = jwtUtils.generateNewToken(userEntity);

        HttpHeaders headers = new HttpHeaders();

        headers.add("x-header-payload-token", String.format("%s.%s", jwt.getParsedParts()[0], jwt.getParsedParts()[1]));
        headers.add("x-signature-token", jwt.getParsedParts()[2].toString());

        HttpServletRequest actualRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // skip eula
        if(FALSE.equals(Boolean.parseBoolean(actualRequest.getHeader("skip-eula")))) {

            // get eula details (standard user)
            if (userEntity.getAcceptedEULA() == null)
                return Constructor.buildResponseObjectLocation(HttpStatus.FORBIDDEN, Messages.Error.EULA_STANDARD_NOT_ACCEPTED, Messages.App.EULA_ENDPOINT, headers);

            // get eula details (manager)
            if((userEntity.getRole().equalsAny(ROLE_MANAGER, ROLE_ADMIN))
                && userEntity.getAcceptedEULAManager() == null)
                return Constructor.buildResponseObjectLocation(HttpStatus.FORBIDDEN, Messages.Error.EULA_MANAGER_NOT_ACCEPTED, Messages.App.EULA_ENDPOINT, headers);
        }

        return Constructor.buildResponseObjectHeader(HttpStatus.OK, UserMapper.MAPPER.toDTO(userEntity), headers);
    }

    public ResponseEntity<?> logout(String token) throws ParseException {
        Integer userId = jwtUtils.getUserIdFromJwtToken(jwtUtils.getDecodedJwt(token));

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
        Token tokenEntity = tokenRepository.findByJwtID(jwt.getHeader().getKeyID()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.RECOVERY_TOKEN_NOT_VALID_NOT_FOUND));

        // check is recovery token
        if (!tokenEntity.isRecoveryToken())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.JWT_NOT_FOR_RECOVERY);

        Integer userIdFromTokenEntity = tokenEntity.getUserId();
        Integer userIdFromTokenString = jwtUtils.getUserIdFromJwtToken(jwt);

        Preconditions.checkArgument(userIdFromTokenEntity.equals(userIdFromTokenString));

        // check user exists
        userRepository.findById(userIdFromTokenEntity).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.RECOVERY_TOKEN_VALID);
    }

    public ResponseEntity<?> newCredentials(UserCredentialsDto userCredentials) throws ParseException {
        validateToken(userCredentials.getToken());

        // get uset details
        User user = userRepository.findByEmail(userCredentials.getEmail()).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        userRepository.saveAndFlush(user);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.USER_CREDENTIALS_UPDATED);
    }

}
