package org.ldcgc.backend.controller.users.impl;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.users.AccountController;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.service.users.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    public ResponseEntity<?> login(UserCredentialsDto userCredentials) throws ParseException, JOSEException {
        return accountService.login(userCredentials);
    }

    public ResponseEntity<?> logout(String token) throws ParseException {
        return accountService.logout(token);
    }

    public ResponseEntity<?> recoverCredentials(UserCredentialsDto userCredentials) throws ParseException, JOSEException {
        return accountService.recoverCredentials(userCredentials);
    }

    public ResponseEntity<?> validateToken(String token) throws ParseException {
        return accountService.validateToken(token);
    }

    public ResponseEntity<?> newCredentials(UserCredentialsDto userCredentials) throws ParseException {
        return accountService.newCredentials(userCredentials);
    }

}
