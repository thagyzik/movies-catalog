package com.moviescatalog.controller;

import com.moviescatalog.config.SecurityConfig;
import com.moviescatalog.dto.AuthRequestDto;
import com.moviescatalog.dto.ErrorResponseDto;
import com.moviescatalog.model.User;
import com.moviescatalog.services.JwtService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Operations related to login to get a JWT token")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    private SecurityConfig securityConfig;
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful. JWT token returned.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"token\": \"your.jwt.token.here\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "Email and password for login", required = true)
            @RequestBody AuthRequestDto request) {
        User user = userService.findByEmail(request.getEmail());
        if (user != null && securityConfig.passwordEncoder().matches(request.getPassword(), user.getPassword())) {
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(Collections.singletonMap("error", "Invalid credentials"));
    }
}
