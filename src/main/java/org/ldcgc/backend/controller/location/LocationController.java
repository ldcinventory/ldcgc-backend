package org.ldcgc.backend.controller.location;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.util.constants.Messages;
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

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_USER;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/locations")
@Tag(name = "Locations", description = "Locations methods with CRUD functions")
public interface LocationController {

    @Operation(summary = "Get all locations (can filter by group)", description = SWAGGER_ROLE_OPERATION_USER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = LocationDto.class)),
            examples = @ExampleObject(name = "Location found", value = Messages.Info.LOCATION_FOUND)
        )
    )
    @GetMapping
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getLocations(
        @Parameter(description = "Filter by groupId")
            @RequestParam(required = false) Integer groupId
    );

    @Operation(summary = "Get specific location", description = SWAGGER_ROLE_OPERATION_USER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LocationDto.class)
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Location not found", value = Messages.Error.LOCATION_NOT_FOUND)
            })
    )
    @GetMapping("/{locationId}")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getLocation(
        @Parameter(description = "LocationId")
            @PathVariable(required = false) Integer locationId,
        @Parameter(description = "Get detailed info from location")
            @RequestParam(required = false, defaultValue = "false") boolean detailed
    );

    @Operation(summary = "Create a new location", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LocationDto.class),
            examples = {
                @ExampleObject(name = "Location updated", value = Messages.Info.LOCATION_CREATED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Location duplicated", value = Messages.Error.LOCATION_EXISTS)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Group not found", value = Messages.Error.GROUP_NOT_FOUND)
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createLocation(
        @Parameter(description = "Location to create")
            @RequestBody(required = false) LocationDto locationDto
    );

    @Operation(summary = "Update existing location", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LocationDto.class),
            examples = {
                @ExampleObject(name = "Location updated", value = Messages.Info.LOCATION_UPDATED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Location not found", value = Messages.Error.LOCATION_NOT_FOUND),
                @ExampleObject(name = "Group not found", value = Messages.Error.GROUP_NOT_FOUND)
            })
    )
    @PutMapping("/{locationId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> updateLocation(
        @Parameter(description = "Location Id")
            @PathVariable Integer locationId,
        @Parameter(description = "Location object to update")
            @RequestBody(required = false) LocationDto locationDto
    );

    @Operation(summary = "Delete an existing location", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Location deleted", value = Messages.Info.LOCATION_DELETED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Location assigned to a group", value = Messages.Error.LOCATION_MAIN_GROUP)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Location not found", value = Messages.Error.LOCATION_NOT_FOUND)
            })
    )
    @DeleteMapping("/{locationId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteLocation(
        @Parameter(description = "Location to delete")
            @PathVariable(required = false) Integer locationId
    );

}
