package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/volunteers")
@Tag(name = "Volunteers", description = "Volunteers methods with CRUD functions")
public interface VolunteerController {

    @Operation(summary = "Get my volunteer")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VolunteerDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer from token not exist", value = Messages.Error.VOLUNTEER_TOKEN_NOT_EXIST),
            })
    )
    @GetMapping("/me")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getMyVolunteer(
        @Parameter(description = "Valid JWT of the user to get own volunteer details", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    @Operation(summary = "Get any volunteer")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VolunteerDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer doesn't exist", value = Messages.Error.VOLUNTEER_NOT_FOUND),
            })
    )
    @GetMapping("/{builderAssistantId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getVolunteer(
        @Parameter(description = "Volunteer Builder Assistant Id")
            @PathVariable String builderAssistantId);

    @Operation(summary = "List volunteers")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = VolunteerDto.class)),
            examples = {
                @ExampleObject(name = "Volunteers found", value = Messages.Info.USER_LISTED, description = "%s will be replaced by the number of volunteers found")
            })
    )
    @GetMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> listVolunteers(
        @Parameter(description = "Page index")
        @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @Parameter(description = "Size of every page (default = 25)")
        @RequestParam(required = false, defaultValue = "25") Integer size,
        @Parameter(description = "Filter to search user name OR last name")
        @RequestParam(required = false) String filterString,
        @Parameter(description = "Volunteer Builder Assistant Id (ignores the other params)")
        @RequestParam(required = false) String builderAssistantId);

    @Operation(summary = "Create a volunteer")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VolunteerDto.class),
            examples = {
                @ExampleObject(name = "Volunteer created", value = Messages.Info.VOLUNTEER_CREATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_409,
        description = SwaggerConfig.HTTP_REASON_409,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer already exists" , value = Messages.Error.VOLUNTEER_ALREADY_EXIST, description = "%s will be replaced with a builder assistant id"),
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createVolunteer(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Volunteer properties", required = true)
            @RequestBody VolunteerDto volunteerDto);

    @Operation(summary = "Update any volunteer")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VolunteerDto.class),
            examples = {
                @ExampleObject(name = "Volunteer updated", value = Messages.Info.VOLUNTEER_UPDATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer doesn't exist", value = Messages.Error.VOLUNTEER_NOT_FOUND),
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_409,
        description = SwaggerConfig.HTTP_REASON_409,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer from token not exist", value = Messages.Error.VOLUNTEER_ID_ALREADY_TAKEN),
            })
    )
    @PutMapping("/{builderAssistantId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateVolunteer(
        @Parameter(description = "Volunteer Builder Assistant Id", required = true)
            @PathVariable String builderAssistantId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Volunteer properties", required = true)
            @RequestBody VolunteerDto volunteerDto);

    @Operation(summary = "Delete any volunteer")
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer deleted", value = Messages.Info.VOLUNTEER_DELETED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer doesn't exist", value = Messages.Error.VOLUNTEER_NOT_FOUND),
            })
    )
    @DeleteMapping("/{builderAssistantId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteVolunteer(
        @Parameter(description = "Volunteer Builder Assistant Id", required = true)
            @PathVariable String builderAssistantId);



}
