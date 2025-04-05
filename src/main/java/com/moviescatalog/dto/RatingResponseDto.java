package com.moviescatalog.dto;

import com.moviescatalog.model.Rating;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RatingResponseDto {

    @Schema(description = "Rate movie", example = "4")
    private int rate;

    @Schema(description = "Movie ID", example = "3")
    private Long movieId;

    @Schema(description = "Name of the movie", example = "Inception")
    private String movieName;

    @Schema(description = "User ID", example = "2")
    private Long userId;

    @Schema(description = "User email", example = "user@gmail.com")
    private String userEmail;

    public static RatingResponseDto fromEntity(Rating rating) {
        RatingResponseDto dto = new RatingResponseDto();
        dto.setRate(rating.getRate());
        dto.setMovieId(rating.getMovie().getId());
        dto.setMovieName(rating.getMovie().getName());
        dto.setUserId(rating.getUser().getId());
        dto.setUserEmail(rating.getUser().getEmail());
        return dto;
    }

}
