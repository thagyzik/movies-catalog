package com.moviescatalog.controller;

import com.moviescatalog.dto.ErrorResponseDto;
import com.moviescatalog.dto.UserDto;
import com.moviescatalog.enums.RoleEnum;
import com.moviescatalog.model.User;
import com.moviescatalog.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to users")
public class UsersController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user with the USER Role as default", description = "Registers a new user in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/registerUser")
    public ResponseEntity<UserDto> registerUser(
            @Parameter(description = "User data", required = true)
            @Valid @RequestBody UserDto userDto) {
        User existingUser = userService.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if(existingUser != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        User userSaved = userService.saveUser(userDto);
        UserDto userDtoResponse = UserDto.fromUser(userSaved);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDtoResponse);
    }

    @Operation(summary = "Update user role", description = "Allows admin to update the role of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/updateRole/{username}/{newRole}")
    public ResponseEntity<UserDto> updateUserRole(
            @Parameter(description = "Username of the user")
            @PathVariable String username,

            @Parameter(description = "New role to assign")
            @PathVariable RoleEnum newRole) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        user.setRole(newRole);
        User updatedUser = userService.updateRole(user);
        UserDto userDto = UserDto.fromUser(updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

}
