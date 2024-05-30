package org.ldcgc.backend.controller.history;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.history.ToolRegisterDto;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.ERegisterStatus;
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

import java.util.List;

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/resources/tools/registers")
@Tag(name = "Tool Register", description = "Tool register methods with CRUD functions")
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
                        @ExampleObject(name = "Tool not found", value = Messages.Error.TOOL_ID_NOT_FOUND),
                        @ExampleObject(name = "Volunteer not found", value = Messages.Error.VOLUNTEER_NOT_FOUND)
                    })
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_400,
            description = SwaggerConfig.HTTP_REASON_400,
            content = @Content(mediaType = "application/json",
                    examples = {
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
    ResponseEntity<?> getAllToolRegisters(
        @Parameter(description = "Status of the register (opened/closed)")
            @RequestParam(required = false) ERegisterStatus status,
        @Parameter(description = "Filter by volunteer name, last name or both (with the same input string)")
            @RequestParam(required = false) String volunteer,
        @Parameter(description = "Filter by tool name or barcode")
            @RequestParam(required = false) String tool,
        @Parameter(description = "Page index (default = 0)")
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @Parameter(description = "Size of every page (default = 25)")
            @RequestParam(required = false, defaultValue = "25") Integer size,
        @Parameter(description = "Sort by any field desired (see fields of ToolRegister class) (default = registerFrom)")
            @RequestParam(required = false, defaultValue = "registerFrom") String sortString,
        @Parameter(description = "Sort asc desc (default = desc)")
            @RequestParam(required = false, defaultValue = "desc") EOrder order
    );

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
    ResponseEntity<?> updateToolRegister(@PathVariable Integer registerId, @RequestBody ToolRegisterDto registerDto);


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
    ResponseEntity<?> getToolRegister(@PathVariable Integer registerId);


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
    ResponseEntity<?> deleteToolRegister(@PathVariable Integer registerId);

    @Operation(summary = "Create multiple tool registers. Insert inRegistration to null to make an OPEN registration")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_204,
            description = SwaggerConfig.HTTP_REASON_204,
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ToolRegisterDto.class)))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_400,
            description = SwaggerConfig.HTTP_REASON_400,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool register repeated tools", value = Messages.Error.TOOL_REGISTER_REPEATED_TOOLS),
                            @ExampleObject(name = "Tool register tool not available", value = Messages.Error.TOOL_REGISTER_TOOL_NOT_AVAILABLE)
                    })
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool not found barcode", value = Messages.Error.TOOL_NOT_FOUND_BARCODE),
                            @ExampleObject(name = "Volunteers BA ids not found", value = Messages.Error.VOLUNTEERS_BAID_NOT_FOUND)
                    })
    )
    @PostMapping("/many")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> createMultipleToolRegisters(@RequestBody List<ToolRegisterDto> toolRegistersDto);

}
