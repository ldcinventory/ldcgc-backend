package org.ldcgc.backend.controller.users;

import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;

import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/accounts")
public interface AccountController {

    @Operation(summary = "Perform a login for a user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Password doesn't match", value = "Password provided for this email doesn't match our records")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not found", value = "User not found")
            })
    )
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody UserDto user) throws ParseException, JOSEException;

    @Operation(summary = "Perform a logout for a user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Logout successful", value = "Logout successful")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not found", value = "User not found")
            })
    )
    @PostMapping("/logout")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> logout(@RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    @Operation(summary = "Send recovery credentials (an email with token in url)")
    @ApiResponse(
        responseCode = "201",
        description = "Email sent",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Email sent", value = "Credentials email sent")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not found", value = "User not found"),
            })
    )
    @PostMapping("/recover")
    ResponseEntity<?> recoverCredentials(@RequestBody UserCredentialsDto userCredentials) throws ParseException, JOSEException;

    @Operation(summary = "Validate recovery token from email")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Token valid", value = "Recovery token valid")
            })
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Recovery token not valid", value = "This recovery token is not valid or is not found"),
                @ExampleObject(name = "JWT not for recovery", value = "This token is not for recover the account. Sorry, mate!"),
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not found", value = "User not found"),
            })
    )
    @GetMapping("/validate")
    ResponseEntity<?> validateToken(@RequestParam(name = "recovery-token") String token) throws ParseException;

    @Operation(summary = "Set new credentials for the user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Credentials updated", value = "User credentials updated")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not found", value = "User not found")
            })
    )
    @PostMapping("/new-credentials")
    ResponseEntity<?> newCredentials(@RequestBody UserCredentialsDto userCredentials) throws ParseException;

}
