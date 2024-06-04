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
import org.ldcgc.backend.util.common.ERoleStatus;
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

import java.time.LocalDate;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;

@Controller
@RequestMapping("/role/register")
@Tag(name = "Role", description = "Role CRUD methods")
public interface RoleRegisterController {

    @Operation(summary = "Get any role register details", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = RoleRegister.class),
            examples = {
                @ExampleObject(name = "Role details", value = Messages.Info.ROLE_REGISTER_FOUND)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Role not found", value = Messages.Error.ROLE_REGISTER_NOT_FOUND)
            })
    )
    @PreAuthorize(ADMIN_LEVEL)
    @GetMapping
    ResponseEntity<?> getRole(
        @Parameter(description = "Role id to get details", required = true)
            @RequestAttribute("Authorization") Integer roleId);

    @Operation(summary = "List existing role registers and filters them", description = SWAGGER_ROLE_OPERATION_ADMIN)
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
        @Parameter(description = "Id from foreign table where role is applied")
            @RequestParam Integer linkedId,
        @Parameter(description = "Foreign table name where role is applied")
            @RequestParam String linkedTableName,
        @Parameter(description = "Role name")
            @RequestParam String role,
        @Parameter(description = "Location id")
            @RequestParam Integer location,
        @Parameter(description = "Date that the role is valid from")
            @RequestParam LocalDate validFrom,
        @Parameter(description = "Date that the role is valid until (included)")
            @RequestParam LocalDate validUntil,
        @Parameter(description = "Current status of the role")
            @RequestParam ERoleStatus status,
        @Parameter(description = "Assigned id (could be volunteer, user...)")
            @RequestParam String assignedId,
        @Parameter(description = "Foreign table for the asignee")
            @RequestParam String assignedTableName);

    @Operation(summary = "Create new role register", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_204,
        description = SwaggerConfig.HTTP_REASON_204,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = RoleRegister.class),
            examples = {
                @ExampleObject(name = "Role created", value = Messages.Info.ROLE_REGISTER_CREATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Role not found", value = Messages.Error.ROLE_REGISTER_NOT_FOUND)
            })
    )
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
