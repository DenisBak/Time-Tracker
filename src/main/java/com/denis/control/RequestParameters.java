package com.denis.control;

public enum RequestParameters {
    USERNAME ("username"),
    PASSWORD ("password"),
    NAME     ("name"),

    FIRST_PASSWORD_REG  ("firstPassword"),
    SECOND_PASSWORD_REG ("secondPassword"),

    DESCRIPTION ("description"),
    START_TIME  ("startTime"),
    END_TIME    ("endTime"),
    DATE        ("date");

    private final String name;

    RequestParameters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
