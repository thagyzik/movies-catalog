package com.moviescatalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class RatingRequestDto {

    @Min(1)
    @Max(5)
    @NotNull(message = "Rate cannot be null")
    private Integer rate;

    @NotNull(message = "Movie ID cannot be null")
    @Schema(description = "Movie ID", example = "3")
    private Long movieId;

}
