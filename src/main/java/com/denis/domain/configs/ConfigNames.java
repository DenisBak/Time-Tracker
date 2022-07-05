package com.denis.domain.configs;

public enum ConfigNames {
    DB ("db"),
    EXCEPTIONS ("exceptions"),
    STATEMENTS("statements"),
    LOGGER_MESSAGES ("loggerMessages");

    private String name;

    ConfigNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
