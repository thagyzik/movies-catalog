package com.moviescatalog.controller;

import com.moviescatalog.dto.ErrorResponseDto;
import com.moviescatalog.dto.RatingRequestDto;
import com.moviescatalog.dto.RatingResponseDto;
import com.moviescatalog.services.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/movies")
@Tag(name = "Ratings", description = "Operations related to rating movies")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Rate a movie", description = "Submits a new rating for a movie by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid rating input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/ratings/")
    public ResponseEntity<?> rateMovie(
            @Parameter(description = "Rating data", required = true)
            @Valid @RequestBody RatingRequestDto ratingDto,
                                       Authentication authentication) {
        ratingService.rateMovie(authentication, ratingDto);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Rating submitted successfully"));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete a rating", description = "Deletes the rating of a movie by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rating deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movie ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/ratings/{movieId}")
    public ResponseEntity<?> deleteRating(
            @Parameter(description = "ID of the movie to remove rating from", required = true)
            @PathVariable Long movieId,
                                          Authentication authentication) {
        ratingService.deleteRatingForMovie(movieId, authentication);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Rating deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user's ratings", description = "Retrieves all ratings made by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ratings retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/ratings/byUser")
    public ResponseEntity<List<RatingResponseDto>> getMyRatings(Authentication authentication) {
        List<RatingResponseDto> ratings = ratingService.getRatingsForUser(authentication);
        return ResponseEntity.ok(ratings);
    }

}
