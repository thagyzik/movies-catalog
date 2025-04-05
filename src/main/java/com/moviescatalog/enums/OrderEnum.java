package com.moviescatalog.enums;

public enum OrderEnum {
    YEAR("releaseYear"),
    NAME("name"),
    CREATED_DATE("createdAt"),
    RATING("rating");

    private final String description;

    OrderEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
