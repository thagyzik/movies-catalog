package com.moviescatalog.enums;

public enum CategoryEnum {
    ACTION("Action"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    THRILLER("Thriller");

    private final String description;

    CategoryEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
