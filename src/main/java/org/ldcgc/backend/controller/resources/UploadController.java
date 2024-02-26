package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_MANAGER;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/resources/upload")
public interface UploadController {

    @Operation(summary = "Update a tool or consumable uploading images for any of them.", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            //oneOf = {@Schema(implementation = ToolDto.class), @Schema(implementation = ConsumableDto.class)},
            schema = @Schema(oneOf = { ToolDto.class, ConsumableDto.class }),
            examples = {
                @ExampleObject(name = "ToolDto", value = "test tool"),
                @ExampleObject(name = "ConsumableDto", value = "test consumable")
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Tool not found", value = Messages.Error.TOOL_NOT_FOUND),
                @ExampleObject(name = "Consumable not found", value = Messages.Error.CONSUMABLE_NOT_FOUND)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_422,
        description = SwaggerConfig.HTTP_422,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Many arguments", value = Messages.Error.UPLOAD_IMAGES_TOO_MANY_ARGUMENTS)
            })
    )
    @PutMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> uploadImages(
        @Parameter(description = "The barcode to upload some images for specific tool")
            @RequestParam(required = false) String toolBarcode,
        @Parameter(description = "The barcode to upload some images for specific consumable")
            @RequestParam(required = false) String consumableBarcode,
        @Parameter(description = "Images to upload")
            @RequestParam("images") MultipartFile[] images
    );

}
