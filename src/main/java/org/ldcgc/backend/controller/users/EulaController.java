package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.util.common.EEULAStatus;
import org.ldcgc.backend.util.retrieving.Messages;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@RequestMapping("/eula")
public interface EulaController {

    @Operation(
        summary = "Get EULA terms document",
        description = "Defines a GET operation to get EULA terms in order to provide user information about the use of this personal data within GC8Inventory service",
        operationId = "getEULA"
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Google docs url" , value = Messages.App.EULA_SELECT_ACTION, description = "%s will be replaced by 'every user' or 'managers and admins', and will be provided a list of available actions.")
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
    @GetMapping
    ResponseEntity<?> getEULA(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    @Operation(
        summary = "Accept/Reject EULA terms document",
        description = "Defines a PUT operation to accept or reject EULA terms",
        operationId = "putEULA"
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Accepted" , value = Messages.Info.EULA_ACCEPTED, description = "%s will be replaced by 'every user' or 'managers and admins'"),
                @ExampleObject(name = "Pending" , value = Messages.Info.EULA_PENDING, description = "%s will be replaced by 'every user' or 'managers and admins'"),
                @ExampleObject(name = "Rejected" , value = Messages.Info.EULA_REJECTED, description = "%s will be replaced by 'every user' or 'managers and admins'. According to the level of the user role, it will be deleted or downgraded.")
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "EULA Action Invalid" , value = Messages.Error.EULA_ACTION_INVALID)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_409,
        description = SwaggerConfig.HTTP_REASON_409,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "EULA Already Accepted" , value = Messages.Info.EULA_ACCEPTED, description = "%s will be replaced by 'every user' or 'managers and admins'")
            })
    )
    @PutMapping
    ResponseEntity<?> putEULA(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "indicate how to proceed with the EULA")
            @RequestParam EEULAStatus action) throws ParseException;

}
