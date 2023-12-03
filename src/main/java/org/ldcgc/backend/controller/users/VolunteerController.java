package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/volunteers")
@Tag(name = "Volunteers", description = "Volunteers methods with CRUD functions")
public interface VolunteerController {

    @Operation(summary = "Create a volunteer")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer created", value = "User registered successfully!")
            })
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer already exists" , value = "There's a volunteer with this id"),
            })
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented" , value = "This endpoint is not implemented yet"),
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createVolunteer(
        @RequestBody VolunteerDto volunteer);

    @Operation(summary = "List volunteers")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = VolunteerDto.class)))
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented" , value = "This endpoint is not implemented yet"),
            })
    )
    @GetMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> listVolunteers(
        @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @RequestParam(required = false, defaultValue = "25") Integer sizeIndex,
        @RequestParam(required = false) String filterString,
        @RequestParam(required = false) String volunteerId);

    @Operation(summary = "Get any volunteer")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VolunteerDto.class))
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer doesn't exists", value = "The volunteer you're searching for with this id couldn't be found"),
            })
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @GetMapping("/{volunteerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getVolunteer(
        @Parameter(description = "Volunteer Id")
        @PathVariable String volunteerId);

    @Operation(summary = "Update any volunteer")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer updated", value = "Volunteer details updated")
            })
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer doesn't exists", value = "The volunteer you're searching for with this id couldn't be found"),
            })
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @PutMapping("/{volunteerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateVolunteer(
        @PathVariable String volunteerId,
        VolunteerDto volunteer);

    @Operation(summary = "Delete any volunteer")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer deleted", value = "Volunteer deleted")
            })
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer doesn't exists", value = "The volunteer you're searching for with this id couldn't be found"),
            })
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @DeleteMapping("/{volunteerId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteVolunteer(
        @PathVariable String volunteerId);

}
