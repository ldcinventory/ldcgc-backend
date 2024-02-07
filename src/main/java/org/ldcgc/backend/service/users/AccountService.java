package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface AccountService {

    ResponseEntity<?> login(UserCredentialsDto user) throws ParseException, JOSEException;
    ResponseEntity<?> logout(String token) throws ParseException;
    ResponseEntity<?> recoverCredentials(UserCredentialsDto userCredentials) throws ParseException, JOSEException;
    ResponseEntity<?> validateToken(String token) throws ParseException;
    ResponseEntity<?> newCredentials(UserCredentialsDto userCredentials) throws ParseException;
    ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) throws ParseException, JOSEException;

}
