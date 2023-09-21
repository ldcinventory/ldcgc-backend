package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> createVolunteer(
            //@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Volunteer object")
            @RequestBody VolunteerDto volunteer);

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> listVolunteers(
            @RequestParam(required = false) String pageIndex,
            @RequestParam(required = false) String sizeIndex,
            @RequestParam(required = false) String filterString,
            @RequestParam(required = false) String barcode);

    @GetMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> getVolunteer(
            @PathVariable String volunteerId);

    @PutMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateVolunteer(
            @PathVariable String volunteerId,
            @RequestBody VolunteerDto volunteer);

    @DeleteMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteVolunteer(
            @PathVariable String volunteerId);

}
