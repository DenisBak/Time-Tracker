package com.denis.domain.factories;

public enum ConfigNames {
    DB ("db"),
    EXCEPTIONS ("exceptions"),
    STATEMENTS("statements");

    private String name;

    ConfigNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
