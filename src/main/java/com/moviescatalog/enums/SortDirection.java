package com.moviescatalog.enums;

public enum SortDirection {
    ASC("ASC"),
    DESC("DESC");

    private final String description;

    SortDirection(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
