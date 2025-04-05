package com.moviescatalog.enums;

public enum UserRoleEnum {
    ADMIN("Admin"),
    USER("User");

    private final String description;

    UserRoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
