package org.ldcgc.backend.controller.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;

@Controller
@RequestMapping("/resources/consumables")
public interface ConsumableController {

    @Operation(summary = "Get any consumable by providing its id.", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ConsumableDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable not found", value = Messages.Error.CONSUMABLE_NOT_FOUND)
            })
    )
    @GetMapping("/{consumableId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> getConsumable(
        @Parameter(description = "Consumable Id to get an existing consumable entity", required = true)
            @PathVariable Integer consumableId);

    @Operation(summary = "Create a new consumable.", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ConsumableDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable already exists", value = Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable id shouldn't be present", value = Messages.Error.CONSUMABLE_ID_SHOULDNT_BE_PRESENT)
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createConsumable(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Consumable object to create", required = true)
            @RequestBody ConsumableDto consumableDto);

    @Operation(summary = "List consumables", description = """
        Get all consumables, paginated and sorted. You can also include 3 filters:
        - name
        - description
        - status
                
        Valid status:
        - Disponible -> ```AVAILABLE```
        - No disponible -> ```NOT_AVAILABLE```
        - En mantenimiento -> ```IN_MAINTENANCE```
        - Dañado -> ```DAMAGED```
        - Nueva -> ```NEW```
        - En desuso -> ```DEPRECATED```
                
        """
        + SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ConsumableDto.class))
        )
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> listConsumables(
        @Parameter(description = "Page index (default = 0)")
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @Parameter(description = "Size of every page (default = 25)")
            @RequestParam(required = false, defaultValue = "25") Integer size,
        @Parameter(description = "Filter to search (see description)")
            @RequestParam(required = false, defaultValue = "") String filter,
        @Parameter(description = "Sort by any field desired (see fields of filtering, are the same as sorting")
            @RequestParam(required = false, defaultValue = "id") String sortField);

    @Operation(summary = "Update a consumable. If another consumable has the barcode, an exception will be thrown.", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ConsumableDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable barcode already exists", value = Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_422,
        description = SwaggerConfig.HTTP_REASON_422,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable barcode used many times", value = Messages.Error.CONSUMABLE_BARCODE_USED_MANY_TIMES)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable doesn't exist", value = Messages.Error.CONSUMABLE_NOT_FOUND)
            })
    )
    @PutMapping("/{consumableId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> updateConsumable(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Consumable object to create", required = true)
            @RequestBody ConsumableDto consumableDto,
        @Parameter(description = "Consumable Id to update", required = true)
            @PathVariable Integer consumableId);

    @Operation(summary = "Delete an existing consumable.", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable deleted", value = Messages.Info.CONSUMABLE_DELETED)
            }
        )
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Consumable not found", value = Messages.Error.CONSUMABLE_NOT_FOUND)
            })
    )
    @DeleteMapping("/{consumableId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteConsumable(
        @Parameter(description = "Consumable Id to delete", required = true)
            @PathVariable Integer consumableId);

    @Operation(summary = "Load the consumables from an excel (XLS) file", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ConsumableDto.class)))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json")
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_422,
        description = SwaggerConfig.HTTP_REASON_422,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Error parsing consumable", value = Messages.Error.EXCEL_PARSE_ERROR),
                @ExampleObject(name = "Error parsing consumable (category)", value = Messages.Error.CATEGORY_SON_NOT_FOUND),
                @ExampleObject(name = "Error parsing consumable (location)", value = Messages.Error.LOCATION_NOT_FOUND_EXCEL),
                @ExampleObject(name = "Error parsing consumable (group)", value = Messages.Error.GROUP_NOT_FOUND_EXCEL),
                @ExampleObject(name = "Error parsing consumable (value)", value = Messages.Error.EXCEL_VALUE_INCORRECT),
                @ExampleObject(name = "Error parsing consumable (type)", value = Messages.Error.EXCEL_CELL_TYPE_INCORRECT)
            })
    )
    @PostMapping("/excel")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> loadExcel(
        @Parameter(description = "The group id in which all the volunteers will be included", required = true)
            @RequestParam Integer groupId,
        @Parameter(description = "The XLS file with all the consumables to upload", required = true)
            @RequestPart MultipartFile file);

}
