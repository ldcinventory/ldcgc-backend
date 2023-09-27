package org.ldcgc.backend.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ldcgc.backend.payload.dto.users.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
@Tag(name = "Users", description = "Users methods with CRUD functions, some for Admin")
public interface UserController {

    // TODO
    //  Read self account GET
    //   |-> (/users/me)
    //  Update self account PUT
    //   |-> (/users/me)
    //  Delete self account DELETE
    //   |-> (/users/me)
    //  -- ADMIN role
    //  Create account POST
    //   |-> (/users)
    //  Read all accounts (can filter by parameters) GET
    //   |-> (/users/?page={pageIndex}&size={sizeIndex}&filter={filterString})
    //  Read specific account GET
    //   |-> (/users/{userId})
    //  Update another account PUT
    //   |-> (/users/{userId})
    //  Delete another account DELETE
    //   |-> (/users/{userId})

    // users

    @Operation(summary = "Get my user")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> getMyUser(@RequestHeader("Authorization") String token);

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
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> updateMyUser(@RequestHeader("Authorization") String token, @RequestBody UserDto user);

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
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteMyUser(@RequestHeader("Authorization") String token);

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
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User already exists" , value = "There's a user with this id or email"),
            })
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented" , value = "This endpoint is not implemented yet"),
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
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> getUser(@PathVariable Integer userId);

    @Operation(summary = "List users (admin)")
    @ApiResponse(
        responseCode = "200",
        description = "OK",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    ResponseEntity<?> listUsers(
        @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
        @RequestParam(required = false, defaultValue = "25") Integer sizeIndex,
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
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User doesn't exists", value = "The user you're searching for with this id doesn't exist"),
            })
    )
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
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
        responseCode = "400",
        description = "Bad request",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "User doesn't exists", value = "The user you're searching for with this id doesn't exist"),
            })
    )
    @ApiResponse(
        responseCode = "501",
        description = "Not implemented",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(name = "Not implemented", value = "This endpoint is not implemented yet"),
            })
    )
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<?> deleteUser(@PathVariable Integer userId);

}
