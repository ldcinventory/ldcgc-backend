package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_USER;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/resources/types")
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
    ResponseEntity<?> getResourceTypes();

    @Operation(summary = "Create a new resource type", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(oneOf = { ToolDto.class, ResourceTypeDto.class }),
            examples = {
                @ExampleObject(name = "Resource type updated", value = Messages.Info.RESOURCE_TYPE_CREATED)
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

    @Operation(summary = "Delete an existing resource type", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(oneOf = { ToolDto.class, ResourceTypeDto.class }),
            examples = {
                @ExampleObject(name = "Resource type deleted", value = Messages.Info.RESOURCE_TYPE_DELETED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_403,
        description = SwaggerConfig.HTTP_403,
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
