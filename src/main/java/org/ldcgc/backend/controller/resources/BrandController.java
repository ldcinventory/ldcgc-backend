package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.category.BrandDto;
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
@RequestMapping("/resources/brands")
@Tag(name = "Brand", description = "Brand methods with CRD functions")
public interface BrandController {

    @Operation(summary = "Get all brands", description = SWAGGER_ROLE_OPERATION_USER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BrandDto.class)),
            examples = @ExampleObject(name = "Brand found", value = Messages.Info.BRAND_FOUND)
        )
    )
    @GetMapping
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getBrands();

    @Operation(summary = "Create a new brand", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = BrandDto.class),
            examples = {
                @ExampleObject(name = "Brand updated", value = Messages.Info.BRAND_CREATED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Brand duplicated", value = Messages.Error.BRAND_EXISTS)
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createBrand(
        @Parameter(description = "Brand to create")
            @RequestBody(required = false) BrandDto brandDto
    );

    @Operation(summary = "Delete an existing brand", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Brand deleted", value = Messages.Info.BRAND_DELETED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Brand locked", value = Messages.Error.BRAND_LOCKED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Brand not found", value = Messages.Error.BRAND_NOT_FOUND)
            })
    )
    @DeleteMapping("/{brandId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteBrand(
        @Parameter(description = "Brand to delete")
            @PathVariable(required = false) Integer brandId
    );

}
