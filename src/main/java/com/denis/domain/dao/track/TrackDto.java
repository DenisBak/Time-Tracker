package com.denis.domain.dao.track;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

public class TrackDto {
    private final int id;
    private final int userId;
    private final String description;
    private final Duration duration;
    private final LocalDate date;

    protected TrackDto(int id, int userId, String description, Duration duration, LocalDate date) {
        assert id > 0;
        assert userId > 0;

        this.id = id;
        this.userId = userId;
        this.description = Objects.requireNonNull(description);
        this.duration = Objects.requireNonNull(duration);
        this.date = Objects.requireNonNull(date);
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
