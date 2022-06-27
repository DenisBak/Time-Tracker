package com.denis.domain;

import com.denis.domain.dao.track.TrackDao;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class Track {
    private static Set<Track> recentTracks = new HashSet<>();

    private final int id;
    private final int userId;
    private final String description;
    private final Duration duration;
    private final LocalDate date;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final TrackDao dao = TrackDao.getInstance();
    private static Logger logger = LogManager.getLogger();


    protected Track(int id, int userId, String description, String startTime, String endTime, String date) { // TODO: 6/27/22 think about builder pattern
        this.id = id;
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
    }

    public static Track createTrack(int userId, String description, String startTime, String endTime, String dateStr) throws DAOException {
        Track track;

        Duration duration = Duration.between(
                LocalTime.parse(Objects.requireNonNull(startTime)),
                LocalTime.parse(Objects.requireNonNull(endTime))
        );
        LocalDate date = LocalDate.parse(dateStr);
        try {
            track = getTrackFromRecentTracks(userId, description, duration);
        } catch (NoSuchElementException e) {
            int id = dao.createTrack(userId, description, duration, date);
            track = new Track(id, userId, description, startTime, endTime, dateStr);
        }

        logger.info("was retrieved track - " + track);
        recentTracks.add(track);
        return track;
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

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", duration=" + duration +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return userId == track.userId && description.equals(track.description) && duration.equals(track.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, description, duration);
    }

    private static Track getTrackFromRecentTracks(int userId, String description, Duration duration) {
        for (Track recentTrack : recentTracks) {
            if (recentTrack.getDescription().equals(description)
                    && recentTrack.getDuration().equals(duration)
                    && recentTrack.getUserId() == userId) {
                return recentTrack;
            }
        }
        throw new NoSuchElementException();
    }
}
