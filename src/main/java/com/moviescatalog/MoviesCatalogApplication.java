package com.moviescatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MoviesCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviesCatalogApplication.class, args);
    }

}
