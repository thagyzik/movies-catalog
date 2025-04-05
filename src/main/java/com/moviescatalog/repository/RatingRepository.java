package com.moviescatalog.repository;

import com.moviescatalog.model.Movie;
import com.moviescatalog.model.Rating;
import com.moviescatalog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByUserAndMovie(User user, Movie movie);
    Optional<Rating> findByUserAndMovie(User user, Movie movie);
    List<Rating> findAllByUser(User user);
    @Query("SELECT r.movie.id, r.rate FROM Rating r WHERE r.movie.id IN :movieIds")
    List<Object[]> findRatingsByMovieIds(@Param("movieIds") List<Long> movieIds);

    default Map<Long, Integer> findRatingMapByMovieIds(List<Long> movieIds) {
        return findRatingsByMovieIds(movieIds).stream()
                                              .collect(Collectors.toMap(
                                                      row -> (Long) row[0],
                                                      row -> (Integer) row[1],
                                                      (r1, r2) -> r1
                                              ));
    }

}
