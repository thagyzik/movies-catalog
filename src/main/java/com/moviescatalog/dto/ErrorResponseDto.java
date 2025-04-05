package com.moviescatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ErrorResponseDto {

    @Schema(example = "Error message")
    private String message;
}
