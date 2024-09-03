package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.app.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.shared.constants.Messages;
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

import static org.ldcgc.backend.app.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.app.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_USER;
import static org.ldcgc.backend.app.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.app.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/resources/types")
@Tag(name = "Resource type", description = "Resource type methods with CRD functions")
public interface ResourceTypeController {

    @Operation(summary = "Get all resource types", description = SWAGGER_ROLE_OPERATION_USER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ResourceTypeDto.class)),
            examples = @ExampleObject(name = "Resource type found", value = Messages.Info.RESOURCE_TYPE_FOUND)
        )
    )
    @GetMapping
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getResourceTypes(
        @Parameter(description = "Name of the resource type (could be partial)")
            @RequestParam(required = false) String name,
        @Parameter(description = "Search by locked (true/false/null for not apply)")
            @RequestParam(required = false) Boolean locked
    );

    @Operation(summary = "Create a new resource type", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ResourceTypeDto.class),
            examples = {
                @ExampleObject(name = "Resource type created", value = Messages.Info.RESOURCE_TYPE_CREATED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Resource type duplicated", value = Messages.Error.RESOURCE_TYPE_EXISTS)
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createResourceType(
        @Parameter(description = "Resource type to create")
            @RequestBody(required = false) ResourceTypeDto resourceTypeDto
    );

    @Operation(summary = "Update resource type", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "No changes", value = Messages.Info.NO_CHANGES_PROCESSED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ResourceTypeDto.class),
            examples = {
                @ExampleObject(name = "Resource type updated", value = Messages.Info.RESOURCE_TYPE_UPDATED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Resource type duplicated", value = Messages.Error.RESOURCE_TYPE_EXISTS)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Resource type not found", value = Messages.Error.RESOURCE_TYPE_NOT_FOUND)
            })
    )
    @PutMapping("/{resourceTypeId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> updateResourceType(
        @Parameter(description = "Update ID to update")
            @PathVariable Integer resourceTypeId,
        @Parameter(description = "Resource type details")
            @RequestBody(required = false) ResourceTypeDto resourceTypeDto);

    @Operation(summary = "Delete an existing resource type", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Resource type deleted", value = Messages.Info.RESOURCE_TYPE_DELETED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Resource type locked", value = Messages.Error.RESOURCE_TYPE_LOCKED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Resource type not found", value = Messages.Error.RESOURCE_TYPE_NOT_FOUND)
            })
    )
    @DeleteMapping("/{resourceId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteResourceType(
        @Parameter(description = "Resource type to delete")
            @PathVariable(required = false) Integer resourceId
    );

}
