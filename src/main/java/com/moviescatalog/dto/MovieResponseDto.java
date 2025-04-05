package com.moviescatalog.dto;

import com.moviescatalog.model.Movie;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Base64;

@Data
public class MovieResponseDto implements Serializable {
    private Long id;
    private String name;
    private Integer releaseYear;
    private String synopsis;
    private String category;
    private LocalDate createdAt;
    private String createdBy;
    private String imageEncoded;

    public static MovieResponseDto fromMovieDto(Movie movie) {
        MovieResponseDto dto = new MovieResponseDto();
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setSynopsis(movie.getSynopsis());
        dto.setCategory(movie.getCategory());
        dto.setCreatedAt(movie.getCreatedAt());
        dto.setCreatedBy(movie.getUser().getUsername());

        if (movie.getImage() != null) {
            String base64 = Base64.getEncoder().encodeToString(movie.getImage());
            dto.setImageEncoded(base64.substring(0, Math.min(base64.length(), 100)) + "...");
        }

        return dto;
    }
}
