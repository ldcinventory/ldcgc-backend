package org.ldcgc.backend.controller.users;

import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.users.UserCredentialsDto;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.util.retrieving.Messages;
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
@Tag(name = "Accounts", description = "Account methods with CRUD functions")
public interface AccountController {

    @Operation(summary = "Perform a login for a user")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Password doesn't match", value = Messages.Error.USER_PASSWORD_DONT_MATCH)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @PostMapping("/login")
    ResponseEntity<?> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials (email + password)", required = true)
            @RequestBody UserCredentialsDto userCredentials) throws ParseException, JOSEException;

    @Operation(summary = "Perform a logout for a user")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Logout successful", value = Messages.Info.LOGOUT_SUCCESSFUL)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @PostMapping("/logout")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> logout(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    @Operation(summary = "Send recovery credentials (an email with token in url)")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Email sent", value = Messages.Info.CREDENTIALS_EMAIL_SENT)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_403,
        description = SwaggerConfig.HTTP_REASON_403,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Forbidden standard", value = Messages.Error.EULA_STANDARD_NOT_ACCEPTED),
                @ExampleObject(name = "Forbidden manager", value = Messages.Error.EULA_MANAGER_NOT_ACCEPTED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND),
            })
    )
    @PostMapping("/recover")
    ResponseEntity<?> recoverCredentials(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials (email)", required = true)
            @RequestBody UserCredentialsDto userCredentials) throws ParseException, JOSEException;

    @Operation(summary = "Validate recovery token from email")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Token valid", value = Messages.Info.RECOVERY_TOKEN_VALID)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Recovery token not valid", value = Messages.Error.RECOVERY_TOKEN_NOT_VALID_NOT_FOUND),
                @ExampleObject(name = "JWT not for recovery", value = Messages.Error.JWT_NOT_FOR_RECOVERY_REFRESH),
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND),
            })
    )
    @GetMapping("/validate")
    ResponseEntity<?> validateToken(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestParam(name = "recovery-token") String token) throws ParseException;

    @Operation(summary = "Set new credentials for the user")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Credentials updated", value = Messages.Info.USER_CREDENTIALS_UPDATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @PostMapping("/new-credentials")
    ResponseEntity<?> newCredentials(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials (email + password)", required = true)
            @RequestBody UserCredentialsDto userCredentials) throws ParseException;

    @Operation(summary = "Set new credentials for the user")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Tokens recreated", value = Messages.Info.TOKEN_REFRESHED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Refresh token not valid", value = Messages.Error.REFRESH_TOKEN_NOT_VALID)
            })
    )
    @PostMapping("/refresh-token")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> refreshToken(
        HttpServletRequest request, HttpServletResponse response,
        @Parameter(description = "Valid refresh JWT of the user to get new token", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String refreshToken) throws ParseException, JOSEException;

}
