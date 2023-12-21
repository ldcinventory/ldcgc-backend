package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;

@Service
public interface AccountService {

    ResponseEntity<?> login(@RequestBody UserCredentialsDto user) throws ParseException, JOSEException;
    ResponseEntity<?> logout(@RequestAttribute("Authorization") String token) throws ParseException;
    ResponseEntity<?> recoverCredentials(@RequestBody UserCredentialsDto userCredentials) throws ParseException, JOSEException;
    ResponseEntity<?> validateToken(@PathVariable String token) throws ParseException;
    ResponseEntity<?> newCredentials(@RequestBody UserCredentialsDto userCredentials) throws ParseException;

}
