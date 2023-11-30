package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.payload.dto.users.UserDto;
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

@Controller
@RequestMapping("/users")
@Tag(name = "Users", description = "Users methods with CRUD functions, some for Admin")
public interface UserController {

    // users

    @Operation(summary = "Get my user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User from token not found", value = "User id or user from token not found, or token is not valid"),
                @ExampleObject(name = "User not found", value = "User not found")
            })
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> getMyUser(@RequestAttribute("Authorization") @UserFromTokenInDb String token);

    @Operation(summary = "Update my user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User updated", value = "User details updated")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = "User not found")
            })
    )
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateMyUser(@RequestAttribute("Authorization") @UserFromTokenInDb String token, @RequestBody UserDto user) throws ParseException;

    @Operation(summary = "Delete my user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User deleted", value = "User deleted")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = "User not found")
            })
    )
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteMyUser(@RequestAttribute("Authorization") @UserFromTokenInDb String token) throws ParseException;

    // admin

    @Operation(summary = "Create a user (admin)")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User created", value = "User registered successfully!")
            })
    )
    @ApiResponse(
        responseCode = "409",
        description = "Conflict",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User exists", value = "There's already a user with this id or email")
            })
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> createUser(@RequestBody UserDto user);

    @Operation(summary = "Get any user (admin)")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = "User not found")
            })
    )
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    ResponseEntity<?> getUser(@PathVariable Integer userId);

    @Operation(summary = "List users (admin)")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    ResponseEntity<?> listUsers(
        @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @RequestParam(required = false, defaultValue = "25") Integer size,
        @RequestParam(required = false) String filterString,
        @RequestParam(required = false) Integer userId);

    @Operation(summary = "Update any user (admin)")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User updated", value = "User details updated")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = "User not found"),
            })
    )
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    ResponseEntity<?> updateUser(@PathVariable Integer userId, @RequestBody UserDto user);

    @Operation(summary = "Delete any user (admin)")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User deleted", value = "User deleted")
            })
    )
    @ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User not found", value = "User not found"),
            })
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteUser(@PathVariable Integer userId);

}
