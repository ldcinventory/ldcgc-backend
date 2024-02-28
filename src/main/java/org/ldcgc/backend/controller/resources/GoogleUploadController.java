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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_MANAGER;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/resources/google-upload")
public interface GoogleUploadController {

    @Operation(summary = "Google Upload. Update a tool or consumable uploading images for any of them.", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(oneOf = { ToolDto.class, ConsumableDto.class }),
            examples = {
                @ExampleObject(name = "Tool updated", value = Messages.Info.TOOL_IMAGES_UPDATED),
                @ExampleObject(name = "Consumable updated", value = Messages.Info.CONSUMABLE_IMAGES_UPDATED)
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
                @ExampleObject(name = "Few arguments", value = Messages.Error.UPLOAD_IMAGES_TOO_FEW_ARGUMENTS),
                @ExampleObject(name = "Many arguments", value = Messages.Error.UPLOAD_IMAGES_TOO_MANY_ARGUMENTS)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_500,
        description = SwaggerConfig.HTTP_500,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Image quality out of range", value = Messages.Error.IMAGE_QUALITY_DEFINITION_OUT_OF_RANGE)
            })
    )
    @PatchMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> uploadImages(
        @Parameter(description = "The barcode to upload some images for specific tool")
            @RequestParam(required = false) String toolBarcode,
        @Parameter(description = "The barcode to upload some images for specific consumable")
            @RequestParam(required = false) String consumableBarcode,
        @Parameter(description = "Clean previous attached images to this resource")
            @RequestParam(required = false, defaultValue = "false") boolean cleanExisting,
        @Parameter(description = "Images to upload")
            @RequestParam("images") MultipartFile[] images
    ) throws GeneralSecurityException, IOException;

    @Operation(summary = "Google Upload. Update a tool or consumable to detach its images.", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(oneOf = { ToolDto.class, ConsumableDto.class }),
            examples = {
                @ExampleObject(name = "Tool untouched", value = Messages.Info.TOOL_UNTOUCHED),
                @ExampleObject(name = "Tool updated", value = Messages.Info.TOOL_IMAGES_UPDATED),
                @ExampleObject(name = "Consumable untouched", value = Messages.Info.CONSUMABLE_UNTOUCHED),
                @ExampleObject(name = "Consumable updated", value = Messages.Info.CONSUMABLE_IMAGES_UPDATED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Tool not found", value = Messages.Error.TOOL_NOT_FOUND),
                @ExampleObject(name = "Tool image not found", value = Messages.Error.TOOL_IMAGE_INFORMED_NOT_FOUND),
                @ExampleObject(name = "Consumable not found", value = Messages.Error.CONSUMABLE_NOT_FOUND),
                @ExampleObject(name = "Consumable image not found", value = Messages.Error.CONSUMABLE_IMAGE_INFORMED_NOT_FOUND)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_422,
        description = SwaggerConfig.HTTP_422,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Few arguments", value = Messages.Error.UPLOAD_IMAGES_TOO_FEW_ARGUMENTS),
                @ExampleObject(name = "Many arguments", value = Messages.Error.UPLOAD_IMAGES_TOO_MANY_ARGUMENTS)
            })
    )
    @PatchMapping("/clean")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> detachImages(
        @Parameter(description = "The barcode to upload some images for specific tool")
            @RequestParam(required = false) String toolBarcode,
        @Parameter(description = "The barcode to upload some images for specific consumable")
            @RequestParam(required = false) String consumableBarcode,
        @Parameter(description = "Clean previous attached images to this resource, if null it'll clean everything")
            @RequestParam(required = false) String[] imageIds
    ) throws GeneralSecurityException;

}
