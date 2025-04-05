package com.moviescatalog.model;

import com.moviescatalog.dto.MovieDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "movies", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "releaseYear"})
})
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer releaseYear;

    private String synopsis;

    @Lob
    private byte[] image;

    private String category;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User user;

    private LocalDate createdAt;

    public static Movie fromDto(MovieDto movieDto) {
        Movie movie = new Movie();
        movie.setName(movieDto.getName());
        movie.setReleaseYear(movieDto.getReleaseYear());
        movie.setSynopsis(movieDto.getSynopsis());
        movie.setImage(movieDto.getImage());
        movie.setCategory(movieDto.getCategory());
        movie.setUser(movieDto.getUser());
        movie.setCreatedAt(movieDto.getCreatedAt());
        return movie;
    }

}
