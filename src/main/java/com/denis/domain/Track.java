package com.denis.domain;

import com.denis.domain.dao.track.TrackDao;
import com.denis.domain.dao.track.TrackDto;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.exceptions.NegativeDurationException;
import com.denis.domain.factories.ConfigFactory;
import com.denis.domain.factories.ConfigNames;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Track {
    private static Set<Track> recentTracks = new HashSet<>();

    private final int id;
    private final int userId;
    private final String description;
    private final Duration duration;
    private final LocalDate date;

    private static final TrackDao dao = TrackDao.getInstance();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static Logger logger = LogManager.getLogger();
    private static Configuration exceptionsConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);


    protected Track(int id, int userId, String description, String startTime, String endTime, String date) { // TODO: 6/27/22 think about builder pattern
        assert id > 0;
        assert userId > 0;

        this.id = id;
        this.userId = userId;
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

    private Track(TrackDto dto) {
        this.id = dto.getId();
        this.userId = dto.getUserId();
        this.description = dto.getDescription();
        this.duration = dto.getDuration();
        this.date = dto.getDate();
    }

    public static Track createTrack(int userId, String description, String startTime, String endTime, String dateStr) throws DomainException {
        Track track;

        Duration duration = Duration.between(
                LocalTime.parse(Objects.requireNonNull(startTime)),
                LocalTime.parse(Objects.requireNonNull(endTime))
        );
        if (duration.isNegative()) {
            throw new NegativeDurationException();
        }
        LocalDate date = LocalDate.parse(dateStr);
        try {
            track = getTrackFromRecentTracks(userId, description, duration);
        } catch (NoSuchElementException e) {
            try {
                int id = dao.createTrack(userId, description, duration, date);
                track = new Track(id, userId, description, startTime, endTime, dateStr);
            } catch (DAOException ex) {
                logger.error(ex);
                throw new DomainException(ex);
            }
        }

        logger.info("was retrieved track - " + track);
        recentTracks.add(track);
        return track;
    }

    public static List<Track> getTracksByUserId(int id) throws DomainException {
        if (id <= 0) {
            exceptionsConfig.setProperty("failedParameter", "Track Id");
            throw new DomainException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        logger.info("start find tracks for user id: " + id);

        List<TrackDto> tracksDto;
        try {
            tracksDto = dao.retrieveTracksDtoByUserId(id);
        } catch (DAOException e) {
            logger.error(e);
            throw new DomainException(e);
        }
        List<Track> tracks = new ArrayList<>();

        for (TrackDto trackDto : tracksDto) {
            tracks.add(new Track(trackDto));
        }
        return tracks;
    }

    public String getStringRepresentation() {
        return getDescription() + " - " + LocalTime.ofSecondOfDay(duration.getSeconds());
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
