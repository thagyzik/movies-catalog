package com.moviescatalog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moviescatalog.enums.OrderEnum;
import com.moviescatalog.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@JsonIgnoreProperties
@Data
public class MovieSearchRequestDto {

    @Size(max = 100, message = "The name must be at most 100 characters")
    @Schema(description = "Name of the movie", example = "Inception")
    private String name;

    @Size(max = 500, message = "The synopsis must be at most 500 characters")
    @Schema(description = "Synopsis of the movie", example = "A sci-fi thriller exploring dream manipulation and the complexity of the subconscious mind.")
    private String synopsis;

    @Size(max = 50, message = "The category must be at most 50 characters")
    @Schema(description = "Category of the movie", example = "Sci-Fi")
    private String category;

    @Schema(description = "Release year of the movie", example = "2010")
    private Integer releaseYear;

    @NotNull(message = "Order by cannot be null")
    private OrderEnum orderBy;
    private SortDirection sortDirection = SortDirection.DESC;

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must be at most 100")
    private Integer size = 10;

    public String generateCacheKey() {
        StringBuilder key = new StringBuilder();

        if (name != null) key.append("name:").append(name).append("|");
        if (synopsis != null) key.append("synopsis:").append(synopsis).append("|");
        if (category != null) key.append("category:").append(category).append("|");
        if (releaseYear != null) key.append("year:").append(releaseYear).append("|");
        if (orderBy != null) key.append("orderBy:").append(orderBy).append("|");
        if (sortDirection != null) key.append("direction:").append(sortDirection).append("|");

        key.append("page:").append(page).append("|");
        key.append("size:").append(size);

        return key.toString();
    }

}
