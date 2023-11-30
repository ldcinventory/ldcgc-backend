package org.ldcgc.backend.controller.users;

import com.nimbusds.jose.JOSEException;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;

@Controller
@RequestMapping("/accounts")
public interface AccountController {

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody UserDto user) throws ParseException, JOSEException;

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> logout(@RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    // Send recovery credentials by POST (send email with token in url)
    @PostMapping("/recover")
    ResponseEntity<?> recoverCredentials(@RequestBody UserCredentialsDto userCredentials) throws ParseException, JOSEException;

    // Validate temp token GET
    @GetMapping("/validate?recover-token={token}")
    ResponseEntity<?> validateToken(@PathVariable String token) throws ParseException;

    // Set new credentials POST (send email + new pass + token in payload)
    @PostMapping("/new-credentials")
    ResponseEntity<?> newCredentials(@RequestBody UserCredentialsDto userCredentials) throws ParseException;

}
