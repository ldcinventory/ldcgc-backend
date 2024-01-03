package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.users.AbsenceDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/absences")
public interface AbsenceController {

    // my user

    @Operation(summary = "Get own user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AbsenceDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @GetMapping("/me/{absenceId}")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getMyAbsence(
        @Parameter(description = "Valid JWT of the user to get absence", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "AbsenceId for getting details", required = true)
            @PathVariable Integer absenceId);

    @Operation(summary = "Get own user absences")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = AbsenceDto.class)))
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
    ResponseEntity<?> listMyAbsences(
        @Parameter(description = "Valid JWT of the user to get absence", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "Date 'from' to filter absences")
            @RequestParam LocalDate dateFrom,
        @Parameter(description = "Date 'to' to filter absences")
            @RequestParam LocalDate dateTo);

    @Operation(summary = "Create own user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AbsenceDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @PostMapping("/me")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> createMyAbsence(
        @Parameter(description = "Valid JWT of the user for creating an absence", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "Absence details", required = true)
            @RequestParam AbsenceDto absenceDto);

    @Operation(summary = "Update own user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AbsenceDto.class))
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
    ResponseEntity<?> updateMyAbsence(
        @Parameter(description = "Valid JWT of the user to update absence", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "Absence details", required = true)
            @RequestParam AbsenceDto absenceDto);

    @Operation(summary = "Clear own user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Info.ABSENCE_DELETED, value = Messages.Info.ABSENCE_DELETED)
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
    @DeleteMapping("/me/{absenceId}")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> clearMyAbsence(
        @Parameter(description = "Valid JWT of the user to clear absence", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "AbsenceId to delete")
            @PathVariable Integer absenceId);

    // managed user

    @Operation(summary = "Get any user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AbsenceDto.class))
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
    ResponseEntity<?> getAbsence(
        @Parameter(description = "VolunteerId to get details")
            @PathVariable Integer volunteerId);

    @Operation(summary = "Get own user absences")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = AbsenceDto.class)))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @GetMapping
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> listAbsences(
        @Parameter(description = "Valid JWT of the user to get absence", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "Date 'from' to filter absences")
            @RequestParam LocalDate dateFrom,
        @Parameter(description = "Date 'to' to filter absences")
            @RequestParam LocalDate dateTo,
        @Parameter(description = "Volunteer Ids (array of one or more ids)")
            @RequestParam Integer[] volunteerIds);

    @Operation(summary = "Create user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AbsenceDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Error.USER_NOT_FOUND, value = Messages.Error.VOLUNTEER_NOT_FOUND)
            })
    )
    @PostMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> createAbsence(
        @Parameter(description = "VolunteerId for creating an absence")
            @PathVariable Integer volunteerId,
        @Parameter(description = "Absence details for this user to create")
            @RequestBody AbsenceDto absenceDto);

    @Operation(summary = "Update user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AbsenceDto.class))
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
    ResponseEntity<?> updateAbsence(
        @Parameter(description = "VolunteerId to update details")
            @PathVariable Integer volunteerId,
        @Parameter(description = "Absence details for this user to update")
            @RequestBody AbsenceDto absenceDto);

    @Operation(summary = "Clear own user absence")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = Messages.Info.ABSENCE_DELETED, value = Messages.Info.ABSENCE_DELETED)
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
    @DeleteMapping("/{volunteerId}/{absenceId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> deleteAbsence(
        @Parameter(description = "VolunteerId to delete from")
            @PathVariable Integer volunteerId,
        @Parameter(description = "AbsenceId to delete")
            @PathVariable Integer absenceId);

}
