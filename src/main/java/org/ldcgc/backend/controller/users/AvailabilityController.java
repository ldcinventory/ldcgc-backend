package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.users.AvailabilityDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/availabilities")
public interface AvailabilityController {

    // my user

    @Operation(summary = "Get own user availability")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AvailabilityDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @GetMapping("/me")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getMyAvailability(
        @Parameter(description = "Valid JWT of the user to get availability", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token);

    @Operation(summary = "Update own user availability")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AvailabilityDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @PutMapping("/me")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?>updateMyAvailability(
        @Parameter(description = "Valid JWT of the user to update availability", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "Availability details", required = true)
            @RequestParam AvailabilityDto availabilityDto);

    @Operation(summary = "Clear own user availability")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Info.AVAILABILITY_DELETED, value = Messages.Info.AVAILABILITY_DELETED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @DeleteMapping("/me")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> clearMyAvailability(
        @Parameter(description = "Valid JWT of the user to clear availability", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token);

    // managed user

    @Operation(summary = "Get any user availability")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AvailabilityDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @GetMapping("/{volunteerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getAvailability(
        @Parameter(description = "VolunteerId to get details")
            @PathVariable Integer volunteerId);

    @Operation(summary = "Update user availability")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AvailabilityDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @PutMapping("/{volunteerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateAvailability(
        @Parameter(description = "VolunteerId to update details")
            @PathVariable Integer volunteerId,
        @Parameter(description = "Availability details for this user to update")
            @RequestBody AvailabilityDto availabilityDto);

    @Operation(summary = "Clear own user availability")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Info.AVAILABILITY_DELETED, value = Messages.Info.AVAILABILITY_DELETED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @DeleteMapping("/{volunteerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> clearAvailability(
        @Parameter(description = "VolunteerId to delete details")
            @PathVariable Integer volunteerId);

}
