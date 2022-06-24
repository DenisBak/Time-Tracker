package com.denis.domain;

import com.denis.domain.dao.track.TrackDao;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Track {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final TrackDao dao = TrackDao.getInstance();

    private final int id; // TODO: 6/24/22 make it final
    private final int userId;
    private final String description;
    private final Duration duration;
    private final LocalDate date;

    public Track(int userId, String description, String startTime, String endTime, String date) throws DomainException {
        this.userId = userId; // TODO: 6/24/22 guard cause
        this.description = Objects.requireNonNull(description);
        LocalTime start = LocalTime.parse(
                Objects.requireNonNull(startTime)
        );
        LocalTime end = LocalTime.parse(
                Objects.requireNonNull(endTime)
        );
        this.duration = Duration.between(start, end);
        this.date = LocalDate.parse(
                Objects.requireNonNull(date), DATE_FORMAT
        );
        try {
            this.id = dao.createTrack(this);
        } catch (DAOException e) {
            throw new DomainException(e);
        }
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
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
}
