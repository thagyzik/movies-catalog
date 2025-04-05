package com.moviescatalog.services;

import com.moviescatalog.dto.RatingRequestDto;
import com.moviescatalog.dto.RatingResponseDto;
import com.moviescatalog.repository.MovieRepository;
import com.moviescatalog.repository.RatingRepository;
import com.moviescatalog.model.Movie;
import com.moviescatalog.model.Rating;
import com.moviescatalog.model.User;
import com.moviescatalog.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    public void rateMovie(Authentication authentication, RatingRequestDto ratingDto) {
        User user = authenticationUtils.getUserFromAuthentication(authentication);
        Movie movie = movieRepository.findById(ratingDto.getMovieId())
                                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found for id: " + ratingDto.getMovieId()));

        if (ratingRepository.existsByUserAndMovie(user, movie)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already rated this movie");
        }

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setRate(ratingDto.getRate());

        ratingRepository.save(rating);
    }

    public void deleteRatingForMovie(Long movieId, Authentication authentication) {
        User user = authenticationUtils.getUserFromAuthentication(authentication);
        Movie movie = movieRepository.findById(movieId)
                                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found for id: " + movieId));

        Rating rating = ratingRepository.findByUserAndMovie(user, movie)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rating not found for this movie"));

        ratingRepository.delete(rating);
    }

    public List<RatingResponseDto> getRatingsForUser(Authentication authentication) {
        User user = authenticationUtils.getUserFromAuthentication(authentication);
        List<Rating> ratings = ratingRepository.findAllByUser(user);
        return ratings.stream()
                      .map(RatingResponseDto::fromEntity)
                      .collect(Collectors.toList());
    }


}
