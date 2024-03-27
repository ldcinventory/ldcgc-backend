package org.ldcgc.backend.controller.history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.util.common.ERegisterStatus;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.MediaType;
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

import java.time.LocalDateTime;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_MANAGER;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/resources/consumables/registers")
public interface ConsumableRegisterController {

    @Operation(summary = "Get consumable register details", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = VolunteerDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Consumable register not found", value = Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND),
            })
    )
    @GetMapping("/{registerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getConsumableRegister(
        @Parameter(description = "Register ID to get details")
            @PathVariable Integer registerId);

    @Operation(summary = "List consumable registers", description = """
        Get all registers from consumable registers, paginated and sorted. You can also include 4 filters:
        - volunteer builder assistant id
        - consumable barcode
        - date from
        - date to
             
        """
        + SWAGGER_ROLE_OPERATION_MANAGER)

    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = ConsumableRegisterDto.class)),
            examples = {
                @ExampleObject(name = "Consumable registers found", value = Messages.Info.USER_LISTED, description = "%s will be replaced by the number of consumable registers found")
            }
        )
    )
    @GetMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> listConsumableRegister(
        @Parameter(description = "Page index (default = 0)")
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @Parameter(description = "Size of every page (default = 25)")
            @RequestParam(required = false, defaultValue = "25") Integer size,
        @Parameter(description = "Volunteer Builder Assistant Id (ignores the other params)")
            @RequestParam(required = false) String volunteer,
        @Parameter(description = "Volunteer Consumable barcode (ignores the other params)")
            @RequestParam(required = false) String consumable,
        @Parameter(description = "Date 'from' to filter absences")
            @RequestParam(required = false) LocalDateTime registerFrom,
        @Parameter(description = "Date 'to' to filter absences")
            @RequestParam(required = false) LocalDateTime registerTo,
        @Parameter(description = "Status of the register (opened/closed)")
            @RequestParam(required = false) ERegisterStatus status,
        @Parameter(description = "Sort by any field desired (see fields of ConsumableRegister class) (default = id)")
            @RequestParam(required = false, defaultValue = "id") String sortField,
        @Parameter(description = "Sort desc or asc (default = true)")
            @RequestParam(required = false, defaultValue = "true") boolean descOrder);

    @Operation(summary = "Create a consumable register.", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ConsumableRegisterDto.class))
    )
    // TODO error messages
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable register not found", value = Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND)
            })
    )@PostMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> createConsumableRegister(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Consumable register object to create", required = true)
            @RequestBody ConsumableRegisterDto consumableRegisterDto);

    @Operation(summary = "Update a consumable register.", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ConsumableRegisterDto.class))
    )
    // TODO error messages
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable register not found", value = Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND)
            })
    )
    @PutMapping("/{registerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateConsumableRegister(
        @Parameter(description = "Consumable register ID to update")
            @PathVariable Integer registerId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Consumable register object to update", required = true)
            @RequestBody ConsumableRegisterDto consumableRegisterDto);

    @Operation(summary = "Delete an existing consumable register", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable register deleted", value = Messages.Info.CONSUMABLE_DELETED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable register not found", value = Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND)
            })
    )
    @DeleteMapping("/{registerId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> deleteConsumableRegister(
        @Parameter(description = "Consumable register ID to delete")
            @PathVariable Integer registerId,
        @Parameter(description = "When deleting a register, undo also stock changes")
            @RequestParam(required = false) boolean undoStockChanges);

}
