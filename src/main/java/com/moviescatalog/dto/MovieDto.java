package com.moviescatalog.dto;

import com.moviescatalog.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Schema(description = "Movie data for creation")
public class MovieDto {

    @Schema(hidden = true)
    private Long id;

    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    @Schema(description = "Name of the movie", example = "Inception")
    private String name;

    @NotNull(message = "ReleaseYear cannot be null")
    @Schema(description = "Year the movie was released", example = "2010")
    private Integer releaseYear;

    @NotNull(message = "Synopsis cannot be null")
    @NotEmpty(message = "Synopsis cannot be empty")
    @Schema(description = "Movie synopsis", example = "A mind-bending thriller about dreams within dreams.")
    private String synopsis;

    @Schema(hidden = true)
    private byte[] image;

    @NotNull(message = "Category cannot be null")
    @NotEmpty(message = "Category cannot be empty")
    @Schema(description = "Movie category", example = "Sci-Fi")
    private String category;

    @Schema(hidden = true)
    private User user;

    @Schema(hidden = true)
    private LocalDate createdAt;
}
