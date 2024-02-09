package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.retrieving.Messages;
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
import org.springframework.web.multipart.MultipartFile;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_MANAGER;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/resources/tools")
public interface ToolController {

    @Operation(summary = "Get any tool by providing its id", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation =  ToolDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool not found", value = Messages.Error.TOOL_NOT_FOUND)
                    })
    )
    @GetMapping("/{toolId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getTool(@PathVariable Integer toolId);

    @Operation(summary = "Get all tools, paginated and sorted.", description = """
        You can also include 4 filters:
        - brand
        - model
        - description
        - status
        
        Valid status:
        - Disponible -> ```AVAILABLE```
        - No disponible -> ```NOT_AVAILABLE```
        - En mantenimiento -> ```IN_MAINTENANCE```
        - Dañado -> ```DAMAGED```
        - Nueva -> ```NEW```
        - En desuso -> ```DEPRECATED```
        
        """ + SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ToolDto.class)),
            examples = {
                @ExampleObject(name = "Tools found", value = Messages.Info.TOOL_LISTED, description = "%s will be replaced by the number of tools found")
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Status not found", value = Messages.Error.STATUS_NOT_FOUND)
            })
    )
    @GetMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getAllTools(@RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                  @RequestParam(required = false, defaultValue = "25") Integer size,
                                  @RequestParam(required = false, defaultValue = "name") String sortField,
                                  @RequestParam(required = false, defaultValue = "") String brand,
                                  @RequestParam(required = false, defaultValue = "") String model,
                                  @RequestParam(required = false, defaultValue = "") String description,
                                  @RequestParam(required = false) String status);

    @Operation(summary = "Create a new tool", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ToolDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_400,
            description = SwaggerConfig.HTTP_REASON_400,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool already exists", value = Messages.Error.TOOL_BARCODE_ALREADY_EXISTS)
                    })
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_400,
            description = SwaggerConfig.HTTP_REASON_400,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool id shouldn't be present", value = Messages.Error.TOOL_ID_SHOULDNT_BE_PRESENT)
                    })
    )
    @PostMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> createTool(@RequestBody ToolDto tool);

    @Operation(summary = "Update a tool. If another tool has the barcode, an exception will be thrown", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ToolDto.class))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool doesn't exist", value = Messages.Error.TOOL_NOT_FOUND)
                    })
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_400,
            description = SwaggerConfig.HTTP_REASON_400,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool barcode already exists", value = Messages.Error.TOOL_BARCODE_ALREADY_EXISTS)
                    })
    )
    @PutMapping("/{toolId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateTool(@PathVariable Integer toolId, @RequestBody ToolDto toolDto);

    @Operation(summary = "Delete an existing tool", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool deleted", value = Messages.Info.TOOL_DELETED)
                    }
            )
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool not found", value = Messages.Error.TOOL_NOT_FOUND)
                    })
    )
    @DeleteMapping("/{toolId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteTool(@PathVariable Integer toolId);

    @Operation(summary = "Upload tools from Excel file", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ToolDto.class)))
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Brand not found", value = Messages.Error.CATEGORY_PARENT_NOT_FOUND),
                            @ExampleObject(name = "Location not found", value = Messages.Error.LOCATION_NOT_FOUND)
                    })
    )
    @PostMapping("/excel")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> uploadToolsExcel(@RequestParam("excel") MultipartFile file);

}
