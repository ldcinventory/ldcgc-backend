package org.ldcgc.backend.service.users;

import com.nimbusds.jose.JOSEException;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;

@Service
public interface AccountService {

    ResponseEntity<?> login(@RequestBody UserDto user) throws ParseException, JOSEException;
    ResponseEntity<?> logout(@RequestAttribute("Authorization") String token);
    ResponseEntity<?> recoverCredentials(@RequestBody UserCredentialsDto userCredentials);
    ResponseEntity<?> validateToken(@PathVariable String token);
    ResponseEntity<?> newCredentials(@RequestBody UserCredentialsDto userCredentials);

}
