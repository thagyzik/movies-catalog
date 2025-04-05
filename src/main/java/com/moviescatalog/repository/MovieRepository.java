package com.moviescatalog.repository;

import com.moviescatalog.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    Optional<Movie> findByNameAndReleaseYear(String name, Integer releaseYear);

}
