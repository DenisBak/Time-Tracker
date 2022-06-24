package com.denis.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Track {
    // TODO: 5/23/22 make restriction on description size (as in Tasks table)

//    private int id;
    private final String description;
    private final Duration duration;
//    private final LocalDate date;

    public Track(String description, String startTime, String endTime) {
        this.description = Objects.requireNonNull(description);
        LocalTime start = LocalTime.parse(
                Objects.requireNonNull(startTime)
        );
        LocalTime end = LocalTime.parse(
                Objects.requireNonNull(endTime)
        );
        this.duration = Duration.between(start, end);

    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }
}
