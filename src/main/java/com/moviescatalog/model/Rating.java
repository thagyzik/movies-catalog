package com.moviescatalog.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "movie_id"})
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

}
