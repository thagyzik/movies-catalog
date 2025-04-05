package com.moviescatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    @Schema(description = "Email", example = "admin@gmail.com")
    private String email;

    @Schema(description = "Password", example = "admin123!")
    private String password;
}
