package com.moviescatalog.services;

import com.moviescatalog.dto.CustomPageResponseDto;
import com.moviescatalog.dto.MovieDto;
import com.moviescatalog.dto.MovieResponseDto;
import com.moviescatalog.dto.MovieSearchRequestDto;
import com.moviescatalog.enums.OrderEnum;
import com.moviescatalog.enums.SortDirection;
import com.moviescatalog.repository.MovieRepository;
import com.moviescatalog.repository.RatingRepository;
import com.moviescatalog.model.Movie;
import com.moviescatalog.model.User;
import com.moviescatalog.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    public MovieResponseDto addMovie(MovieDto movieDto, Authentication authentication) {
        Optional<Movie> existing = movieRepository.findByNameAndReleaseYear(
                movieDto.getName(),
                movieDto.getReleaseYear()
        );

        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Movie already exists: " + movieDto.getName());
        }

        User user = authenticationUtils.getUserFromAuthentication(authentication);
        movieDto.setCreatedAt(LocalDate.now());
        movieDto.setUser(user);

        Movie movie = Movie.fromDto(movieDto);
        Movie result = movieRepository.save(movie);
        return MovieResponseDto.fromMovieDto(result);
    }

    public MovieResponseDto updateMovie(MovieDto movieDto, Long id) {
        Movie existingMovie = movieRepository.findById(id)
                                             .orElseThrow(() -> new ResponseStatusException(
                                                     HttpStatus.NOT_FOUND, "Movie not found for id: " + id));

        Optional<Movie> duplicate = movieRepository.findByNameAndReleaseYear(
                movieDto.getName(), movieDto.getReleaseYear()
        ).filter(m -> !m.getId().equals(id));

        if (duplicate.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                                              "Movie already exists with same data: " + movieDto.getName());
        }

        Optional.ofNullable(movieDto.getName()).ifPresent(existingMovie::setName);
        Optional.ofNullable(movieDto.getReleaseYear()).ifPresent(existingMovie::setReleaseYear);
        Optional.ofNullable(movieDto.getSynopsis()).ifPresent(existingMovie::setSynopsis);
        Optional.ofNullable(movieDto.getImage()).ifPresent(existingMovie::setImage);
        Optional.ofNullable(movieDto.getCategory()).ifPresent(existingMovie::setCategory);
        Optional.ofNullable(movieDto.getCreatedAt()).ifPresent(existingMovie::setCreatedAt);
        Optional.ofNullable(movieDto.getImage()).ifPresent(existingMovie::setImage);

        Movie updated = movieRepository.save(existingMovie);
        return MovieResponseDto.fromMovieDto(updated);
    }

    public String deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found for id: " + id);
        }

        movieRepository.deleteById(id);

        return movie.get().getName();
    }

    @Cacheable(value = "moviesListCache", key = "#movieSearchRequestDto.generateCacheKey()")
    public CustomPageResponseDto<MovieResponseDto> searchMovies(MovieSearchRequestDto movieSearchRequestDto) {
        Pageable pageable = PageRequest.of(
                movieSearchRequestDto.getPage(),
                movieSearchRequestDto.getSize(),
                movieSearchRequestDto.getOrderBy() == OrderEnum.RATING ? Sort.unsorted() : Sort.by(Sort.Direction.valueOf(movieSearchRequestDto.getSortDirection().name()),
                                                                                                   movieSearchRequestDto.getOrderBy().getDescription()));

        Specification<Movie> spec = getSpecificationFilter(movieSearchRequestDto);

        Page<Movie> pageResult = movieRepository.findAll(spec, pageable);

        List<Movie> sortedMovies = pageResult.getContent();

        if (movieSearchRequestDto.getOrderBy() == OrderEnum.RATING) {
            Map<Long, Integer> movieRatingMap = ratingRepository.findRatingMapByMovieIds(
                    sortedMovies.stream().map(Movie::getId).collect(Collectors.toList())
            );

            Comparator<Movie> ratingComparator = Comparator.comparing(
                    movie -> movieRatingMap.getOrDefault(movie.getId(), 0)
            );

            if (movieSearchRequestDto.getSortDirection() == SortDirection.DESC) {
                ratingComparator = ratingComparator.reversed();
            }

            sortedMovies = sortedMovies.stream()
                                       .sorted(ratingComparator)
                                       .collect(Collectors.toList());
        }

        List<MovieResponseDto> movieDtos = sortedMovies
                                                   .stream()
                                                   .map(MovieResponseDto::fromMovieDto)
                                                   .collect(Collectors.toList());

        return new CustomPageResponseDto<>(
                movieDtos,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    private Specification<Movie> getSpecificationFilter(MovieSearchRequestDto movieSearchRequestDto) {
        Specification<Movie> spec = Specification.where(null);

        if (movieSearchRequestDto.getName() != null && !movieSearchRequestDto.getName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                                    cb.like(cb.lower(root.get("name")), "%" + movieSearchRequestDto.getName().toLowerCase() + "%")
            );
        }

        if (movieSearchRequestDto.getSynopsis() != null && !movieSearchRequestDto.getSynopsis().isBlank()) {
            spec = spec.and((root, query, cb) ->
                                    cb.like(cb.lower(root.get("synopsis")), "%" + movieSearchRequestDto.getSynopsis().toLowerCase() + "%")
            );
        }

        if (movieSearchRequestDto.getCategory() != null && !movieSearchRequestDto.getCategory().isBlank()) {
            spec = spec.and((root, query, cb) ->
                                    cb.equal(cb.lower(root.get("category")), movieSearchRequestDto.getCategory().toLowerCase())
            );
        }

        if (movieSearchRequestDto.getReleaseYear() != null) {
            spec = spec.and((root, query, cb) ->
                                    cb.equal(root.get("releaseYear"), movieSearchRequestDto.getReleaseYear())
            );
        }
        return spec;
    }

}
