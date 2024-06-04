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
import org.ldcgc.backend.db.model.users.RoleRegister;
import org.ldcgc.backend.payload.dto.users.RoleRegisterDto;
import org.ldcgc.backend.util.constants.Messages;
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

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;

@Controller
@RequestMapping("/role")
@Tag(name = "Role", description = "Role CRUD methods")
public interface RoleController {

    @Operation(summary = "Get any role details", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = RoleRegister.class),
            examples = {
                @ExampleObject(name = "Role details", value = Messages.Info.ROLE_FOUND)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Role not found", value = Messages.Error.ROLE_NOT_FOUND)
            })
    )
    @PreAuthorize(ADMIN_LEVEL)
    @GetMapping
    ResponseEntity<?> getRole(
        @Parameter(description = "Role id to get details", required = true)
            @RequestAttribute("Authorization") Integer roleId);

    @Operation(summary = "List existing roles and filters them", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = RoleRegister.class)),
            examples = {
                @ExampleObject(name = "Role details", value = Messages.Info.ROLES_FOUND)
            })
    )
    @PreAuthorize(ADMIN_LEVEL)
    @GetMapping
    ResponseEntity<?> listRoles(
        @Parameter(description = "Name of the role")
            @RequestParam String name,
        @Parameter(description = "Description of the role")
            @RequestParam String description);

    @PreAuthorize(ADMIN_LEVEL)
    @PostMapping
    ResponseEntity<?> createRole(
        @Parameter(description = "Role details")
            @RequestBody RoleRegisterDto roleRegisterDto);

    @PreAuthorize(ADMIN_LEVEL)
    @PutMapping("/{roleId}")
    ResponseEntity<?> updateRole(
        @Parameter(description = "Role id to update details")
            @PathVariable String roleId,
        @Parameter(description = "Role details")
            @RequestBody RoleRegisterDto roleRegisterDto);

    @PreAuthorize(ADMIN_LEVEL)
    @DeleteMapping("/{roleId}")
    ResponseEntity<?> deleteRole(
        @Parameter(description = "Roled id to delete")
            @PathVariable String roleId);

}
