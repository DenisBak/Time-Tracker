package com.denis.domain.dao.track;

import java.time.Duration;
import java.time.LocalDate;

public class TrackDto {
    private final int id;
    private final int userId;
    private final String description;
    private final Duration duration;
    private final LocalDate date;

    protected TrackDto(int id, int userId, String description, Duration duration, LocalDate date) {
        this.id = id;
        this.description = description;
        this.duration = duration;
        this.date = date;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "TrackDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", date=" + date +
                '}';
    }
}
