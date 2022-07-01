package com.denis.domain.dao;

public enum ColumnNames {
    // Users table
    USER_ID  ("UserID"),
    USERNAME ("UserName"),
    PASSWORD ("Password"),
    NAME     ("NAME"),

    // Tracks table
    TRACK_ID    ("TrackID"),
    DESCRIPTION ("Description"),
    DURATION    ("Duration"),
    DATE        ("Date");

    private String columnName;
    ColumnNames(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
