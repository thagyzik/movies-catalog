package com.moviescatalog.controller;

import com.moviescatalog.dto.*;
import com.moviescatalog.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/movies")
@Tag(name = "Movies", description = "Operations related to movies")
public class MoviesController {

    @Autowired
    private MovieService movieService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Add a new movie",
            description = "Adds a new movie. \n\n⚠️ Note: Swagger UI does not support sending `multipart/form-data` with both JSON and file. Use Postman or another tool to test this endpoint properly.\n\nExpected form:\n- `movie` (JSON in string format)\n- `image` (optional file)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping(value = "/addMovie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponseDto> addMovie(
            @Parameter(description = "Movie data in JSON format", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("movie") @Valid MovieDto movieDto,

            @Parameter(description = "Movie image file (optional)", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                movieDto.setImage(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        MovieResponseDto savedMovie = movieService.addMovie(movieDto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Update a movie",
            description = "Updates a movie and optionally replaces its image. "
                          + "\n\n⚠️ Note: Swagger UI does not support sending `multipart/form-data` with both JSON and file. Use Postman or another tool to test this endpoint properly.\n\nExpected form:\n- `movie` (JSON in string format)\n- `image` (optional file)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping(value = "/updateMovie/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieResponseDto> updateMovie(
            @Parameter(description = "Updated movie data", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("movie") @Valid MovieDto movieDto,

            @Parameter(description = "Movie ID", required = true)
            @PathVariable Long id,

            @Parameter(description = "Updated image file (optional)", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                movieDto.setImage(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        MovieResponseDto savedMovie = movieService.updateMovie(movieDto, id);
        return ResponseEntity.status(HttpStatus.OK).body(savedMovie);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Delete a movie", description = "Deletes a movie by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))

    })
    @DeleteMapping("/deleteMovie/{id}")
    public ResponseEntity<Map<String, String>> deleteMovie(
            @Parameter(description = "ID of the movie to delete", required = true)
            @PathVariable Long id) {
        String deletedMovie = movieService.deleteMovie(id);
        return ResponseEntity.ok(Collections.singletonMap("message", String.format("Movie deleted successfully: %s", deletedMovie)));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Search movies", description = "Searches for movies using filters such as name, category, year, etc. "
                                                        + "Response data is cached on Redis for 7 days.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<CustomPageResponseDto<MovieResponseDto>> searchMovies(
            @Parameter(description = "Search parameters")
            @Valid MovieSearchRequestDto searchDto) {
        CustomPageResponseDto<MovieResponseDto> results = movieService.searchMovies(searchDto);
        return ResponseEntity.ok(results);
    }

}
