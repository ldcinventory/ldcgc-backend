package org.ldcgc.backend.controller.history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/resources/tools/registers")
public interface ToolRegisterController {

    @Operation(summary = "Create tool register. Insert inRegistration to null to make an OPEN registration")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ToolRegisterDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                        @ExampleObject(name = "Tool not found", value = Messages.Error.TOOL_NOT_FOUND),
                        @ExampleObject(name = "Volunteer not found", value = Messages.Error.VOLUNTEER_NOT_FOUND)
                    })
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_400,
            description = SwaggerConfig.HTTP_REASON_400,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Too many volunteers", value = Messages.Error.TOOL_REGISTER_TOO_MANY_VOLUNTEERS),
                            @ExampleObject(name = "Incorrect BA id", value = Messages.Error.TOOL_REGISTER_INCORRECT_BUILDER_ASSISTANT_ID)
                    })
    )
    @PostMapping()
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> createToolRegister(@RequestBody ToolRegisterDto toolRegisterDto);

    @Operation(summary = "Get all the registers.",
            description = "Add opened/closed as the filter string to select OPENED (no in registration date) or CLOSED (with in registration date) registers. Leave empty to get all.")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ToolRegisterDto.class)))
    )
    @GetMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getAllRegisters(@RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                      @RequestParam(required = false, defaultValue = "25") Integer size,
                                      @RequestParam(required = false, defaultValue = "registerFrom") String sortString,
                                      @RequestParam(required = false) String filterString);

    @Operation(summary = "Update a register. Insert inRegistration to not null to CLOSE a registration (if it was opened)")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ToolRegisterDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool register not found", value = Messages.Error.TOOL_REGISTER_NOT_FOUND)
                    })
    )
    @PutMapping("/{registerId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> updateRegister(@PathVariable Integer registerId, @RequestBody ToolRegisterDto registerDto);


    @Operation(summary = "Get a specific register")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ToolRegisterDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool register not found", value = Messages.Error.TOOL_REGISTER_NOT_FOUND)
                    })
    )
    @GetMapping("/{registerId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getRegister(@PathVariable Integer registerId);


    @Operation(summary = "Delete a tool register")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ToolRegisterDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool register not found", value = Messages.Error.TOOL_REGISTER_NOT_FOUND)
                    })
    )
    @DeleteMapping("/{registerId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteRegister(@PathVariable Integer registerId);

}
