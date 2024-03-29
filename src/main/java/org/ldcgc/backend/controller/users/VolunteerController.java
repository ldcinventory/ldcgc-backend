package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.users.VolunteerDto;
import org.ldcgc.backend.util.constants.Messages;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_MANAGER;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;

@Controller
@RequestMapping("/volunteers")
@Tag(name = "Volunteers", description = "Volunteers methods with CRUD functions")
public interface VolunteerController {

    @Operation(summary = "Get my volunteer", description = SWAGGER_ROLE_OPERATION_MANAGER)
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
                @ExampleObject(name = "Volunteer from token not exist", value = Messages.Error.VOLUNTEER_TOKEN_NOT_EXIST),
            })
    )
    @GetMapping("/me")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getMyVolunteer(
        @Parameter(description = "Valid JWT of the user to get own volunteer details", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    @Operation(summary = "Get any volunteer", description = SWAGGER_ROLE_OPERATION_MANAGER)
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
                @ExampleObject(name = "Volunteer doesn't exist", value = Messages.Error.VOLUNTEER_NOT_FOUND),
            })
    )
    @GetMapping("/{builderAssistantId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getVolunteer(
        @Parameter(description = "Volunteer Builder Assistant Id", in = ParameterIn.PATH, name = "builderAssistantId", schema = @Schema(type = "string"))
            @PathVariable String builderAssistantId);

    @Operation(summary = "Create a volunteer", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VolunteerDto.class),
            examples = {
                @ExampleObject(name = "Volunteer created", value = Messages.Info.VOLUNTEER_CREATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_409,
        description = SwaggerConfig.HTTP_REASON_409,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Volunteer already exists" , value = Messages.Error.VOLUNTEER_ALREADY_EXIST, description = "%s will be replaced with a builder assistant id"),
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createVolunteer(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Volunteer properties", required = true)
            @RequestBody VolunteerDto volunteer);

    @Operation(summary = "List volunteers", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            array = @ArraySchema(schema = @Schema(implementation = VolunteerDto.class)),
            examples = {
                @ExampleObject(name = "Volunteers found", value = Messages.Info.USER_LISTED, description = "%s will be replaced by the number of volunteers found")
            })
    )
    @GetMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> listVolunteers(
        @Parameter(description = "Page index")
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @Parameter(description = "Size of every page (default = 25)")
            @RequestParam(required = false, defaultValue = "25") Integer size,
        @Parameter(description = "Filter to search user name OR last name")
            @RequestParam(required = false) String filterString,
        @Parameter(description = "Volunteer Builder Assistant Id (ignores the other params)")
            @RequestParam(required = false) String builderAssistantId,
        @Parameter(description = "Sort by any field desired (see fields of Volunteer class)")
            @RequestParam(required = false, defaultValue = "id") String sortField);


    @Operation(summary = "Update any volunteer", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = VolunteerDto.class),
            examples = {
                @ExampleObject(name = "Volunteer updated", value = Messages.Info.VOLUNTEER_UPDATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Volunteer doesn't exist", value = Messages.Error.VOLUNTEER_NOT_FOUND),
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_409,
        description = SwaggerConfig.HTTP_REASON_409,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Volunteer from token not exist", value = Messages.Error.VOLUNTEER_ID_ALREADY_TAKEN),
            })
    )
    @PutMapping("/{builderAssistantId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateVolunteer(
        @Parameter(description = "Volunteer Builder Assistant Id", required = true)
            @PathVariable String builderAssistantId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Volunteer properties", required = true)
            @RequestBody VolunteerDto volunteer);

    @Operation(summary = "Delete any volunteer", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Volunteer deleted", value = Messages.Info.VOLUNTEER_DELETED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Volunteer doesn't exist", value = Messages.Error.VOLUNTEER_NOT_FOUND),
            })
    )
    @DeleteMapping("/{builderAssistantId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteVolunteer(
        @Parameter(description = "Volunteer Builder Assistant Id", required = true)
            @PathVariable String builderAssistantId);

    @Operation(summary = "Upload volunteers from CSV", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_204,
        description = SwaggerConfig.HTTP_REASON_204,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {@ExampleObject(name = "Volunteers created from file", value = Messages.Info.CSV_VOLUNTEERS_CREATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_400,
        description = SwaggerConfig.HTTP_REASON_400,
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = {
                @ExampleObject(name = "Validation name error", value = Messages.Error.CSV_NAME_ERROR),
                @ExampleObject(name = "Validation last name error", value = Messages.Error.CSV_LAST_NAME_ERROR),
                @ExampleObject(name = "Validation BA Identifier error", value = Messages.Error.CSV_BA_IDENTIFIER_ERROR),
                @ExampleObject(name = "Validation user duplicate error", value = Messages.Error.CSV_VOLUNTEER_DUPLICATED),
                @ExampleObject(name = "Validation error CSV", value = Messages.Error.CSV_PROCESS_ERROR)
            })
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> uploadVolunteers(
        @Parameter(description = "The group id in which all the volunteers will be included", required = true)
            @RequestParam Integer groupId,
        @Parameter(description = "The CSV file with all the volunteers to upload", required = true)
            @RequestPart MultipartFile document);

}
