package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
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

import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;

@Controller
@RequestMapping("/resources/tools")
public interface ToolController {


    @Operation(summary = "Get any tool by providing its id.")
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
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getTool(@PathVariable Integer toolId);

    @Operation(summary = "Create a new tool.")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ToolDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Already exists",
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool already exists", value = "There is another tool with the same barcode (123456789) in the database. Please make sure that the barcode is unique")
                    })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createTool(@RequestBody ToolDto tool);

    @Operation(summary = "Update a tool. If the tool is not found, it gets inserted instead.")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ToolDto.class))
    )
    @PutMapping("/{toolId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> updateTool(@PathVariable Integer toolId, @RequestBody ToolDto toolDto);

    @Operation(summary = "Delete an existing tool.")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool deleted", value = "Tool deleted.")
                    }
            )
    )
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_404,
            description = SwaggerConfig.HTTP_REASON_404,
            content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "Tool not found", value = "Tool with id 1 not found")
                    })
    )
    @DeleteMapping("/{toolId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteTool(@PathVariable Integer toolId);

    @Operation(summary = "Get all tools, paginated and sorted. You can also include 4 filters: brand, model, description and status")
    @ApiResponse(
            responseCode = SwaggerConfig.HTTP_200,
            description = SwaggerConfig.HTTP_REASON_200,
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ToolDto.class))
            )
    )
    @GetMapping()
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getAllTools(@RequestParam Integer pageIndex, @RequestParam Integer size, @RequestParam String filterString);
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
                            @ExampleObject(name = "Brand not found", value = "Brand Hammer not found. Please, enter a valid Brand."),
                            @ExampleObject(name = "Location not found", value = "Location Baul2 not found. Please, enter a valid Location.")
                    })
    )
    @PostMapping("/excel")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> uploadToolsExcel(@RequestParam("excel") MultipartFile file);

}
