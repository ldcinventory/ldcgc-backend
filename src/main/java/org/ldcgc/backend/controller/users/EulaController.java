package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.util.common.EEULAStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;

@Controller
@RequestMapping("/eula")
public interface EulaController {

    @Operation(
        summary = "Get EULA terms document",
        description = "Defines a GET operation to get EULA terms in order to provide user information about the use of this personal data within iPreach service",
        operationId = "getEULA"
    )
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Google docs url" , value = "https://docs.google.com/document/d/e/2PACX-1vTMAT1BQXKqh0zNooCJPFCWHYP7lXUGXdVemuGbZt9DgkZIoVoBwLPnx7DBzjwyJ0LxCpNfRKUA3nfl/pub?embedded=true")
            })
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "JWT not included" , value = "The petition must contain a token"),
                @ExampleObject(name = "JWT not valid" , value = "The token provided is not valid, or the user has already accepted the EULA terms")
            })
    )
    @GetMapping
    ResponseEntity<?> getEULA(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestAttribute("Authorization") String token) throws ParseException;

    @Operation(
        summary = "Accept/Reject EULA terms document",
        description = "Defines a PUT operation to accept or reject EULA terms",
        operationId = "putEULA"
    )
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Confirmed" , value = "Account confirmed"),
                @ExampleObject(name = "Removed" , value = "Account removed (link provided to activate again)"),
                @ExampleObject(name = "Deleted" , value = "Account deleted permanently")
            })
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "JWT not included" , value = "The petition must contain a token"),
                @ExampleObject(name = "JWT not valid" , value = "The token provided is not valid, or the user has already accepted the EULA terms")
            })
    )
    @PutMapping
    ResponseEntity<?> putEULA(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestAttribute("Authorization") String token,
        @Parameter(description = "indicate how to proceed with the EULA")
            @RequestParam EEULAStatus action) throws ParseException;

}
