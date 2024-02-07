package org.ldcgc.backend.controller.users;

import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.configuration.SwaggerConfig;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.ldcgc.backend.validator.annotations.UserFromTokenInDb;
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

import java.text.ParseException;

import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_ADMIN;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_MANAGER;
import static org.ldcgc.backend.configuration.SwaggerConfig.SWAGGER_ROLE_OPERATION_USER;
import static org.ldcgc.backend.security.Authority.Role.ADMIN_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.MANAGER_LEVEL;
import static org.ldcgc.backend.security.Authority.Role.USER_LEVEL;

@Controller
@RequestMapping("/users")
@Tag(name = "Users", description = "Users methods with CRUD functions, some for Admin")
public interface UserController {

    // users

    @Operation(summary = "Get my user", description = SWAGGER_ROLE_OPERATION_USER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User from token not found", value = Messages.Error.USER_NOT_FOUND_TOKEN),
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @GetMapping("/me")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> getMyUser(
        @Parameter(description = "Valid JWT of the user to get details", required = true)
        @RequestAttribute("Authorization") @UserFromTokenInDb String token);

    @Operation(summary = "Update my user", description = SWAGGER_ROLE_OPERATION_USER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User updated", value = Messages.Info.USER_UPDATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @PutMapping("/me")
    @PreAuthorize(USER_LEVEL)
    ResponseEntity<?> updateMyUser(
        @Parameter(description = "Valid JWT of the user to update", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User properties and volunteer's (optional)", required = true)
            @RequestBody UserDto user) throws ParseException, JOSEException;

    @Operation(summary = "Delete my user", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User deleted", value = Messages.Info.USER_DELETED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @DeleteMapping("/me")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteMyUser(
        @Parameter(description = "Valid JWT of the user to delete", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    // admin

    @Operation(summary = "Create a user (admin)", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User created", value = Messages.Info.USER_CREATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_409,
        description = SwaggerConfig.HTTP_REASON_409,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User exists", value = Messages.Error.USER_ALREADY_EXIST)
            })
    )
    @PostMapping
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> createUser(
        @Parameter(description = "Valid JWT of the user to get details", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User properties and volunteer's (optional)", required = true)
            @RequestBody UserDto user);

    @Operation(summary = "Get any user (manager)", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND)
            })
    )
    @GetMapping("/{userId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> getUser(
        @Parameter(description = "User id", required = true)
            @PathVariable Integer userId);

    @Operation(summary = "List users (manager)", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
    )
    @GetMapping
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> listUsers(
        @Parameter(description = "Page index")
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @Parameter(description = "Size of every page (default = 25)")
            @RequestParam(required = false, defaultValue = "25") Integer size,
        @Parameter(description = "Filter to search user email")
            @RequestParam(required = false) String filterString,
        @Parameter(description = "User Id (ignores the other params)")
            @RequestParam(required = false) Integer userId);

    @Operation(summary = "Update any user (manager)", description = SWAGGER_ROLE_OPERATION_MANAGER)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_201,
        description = SwaggerConfig.HTTP_REASON_201,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User updated", value = Messages.Info.USER_UPDATED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND),
            })
    )
    @PutMapping("/{userId}")
    @PreAuthorize(MANAGER_LEVEL)
    ResponseEntity<?> updateUser(
        @Parameter(description = "Valid JWT of the user to get details", required = true)
            @RequestAttribute("Authorization") @UserFromTokenInDb String token,
        @Parameter(description = "User id", required = true)
            @PathVariable Integer userId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User properties and volunteer's (optional)", required = true)
            @RequestBody UserDto user) throws ParseException, JOSEException;

    @Operation(summary = "Delete any user (admin)", description = SWAGGER_ROLE_OPERATION_ADMIN)
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_200,
        description = SwaggerConfig.HTTP_REASON_200,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User deleted", value = Messages.Info.USER_DELETED)
            })
    )
    @ApiResponse(
        responseCode = SwaggerConfig.HTTP_404,
        description = SwaggerConfig.HTTP_REASON_404,
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = Messages.Error.USER_NOT_FOUND),
            })
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize(ADMIN_LEVEL)
    ResponseEntity<?> deleteUser(
        @Parameter(description = "User id", required = true)
            @PathVariable Integer userId);

}
