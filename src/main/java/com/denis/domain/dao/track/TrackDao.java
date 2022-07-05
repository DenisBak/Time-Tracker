package com.denis.domain.dao.track;

import com.denis.domain.dao.ConnectionFactory;
import com.denis.domain.dao.Dao;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.Track;

import static com.denis.domain.dao.ColumnNames.*;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrackDao extends Dao {
    private static TrackDao instance;

    public static TrackDao getInstance() {
        if (instance == null) {
            instance = new TrackDao();
        }
        return instance;
    }

    public int createTrack(Track track) throws DAOException {
        if (track == null) {
            exceptionsConfig.setProperty("failedParameter", "Track");
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }
        return createTrack(track.getUserId(), track.getDescription(), track.getDuration(), track.getDate());
    }

    public int createTrack(int userId, String description, Duration duration, LocalDate date) throws DAOException {
        if (userId <= 0 || description == null || duration == null || date == null) {
            String failedParam;
            if (userId <= 0)                failedParam = "User Id";
            else if (description == null)   failedParam = "Description";
            else if (duration == null)      failedParam = "Duration";
            else                            failedParam = "Date";
            exceptionsConfig.setProperty("failedParameter", failedParam);
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        String createTrackStatement;
        PreparedStatement statement = null;

        try {
            createTrackStatement = statementsConfig.getString("createTrack");
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(createTrackStatement, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, userId);
            statement.setString(2, description);
            statement.setString(3, duration.toString());
            statement.setDate(4, Date.valueOf(date));

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            return rs.getInt(1); // TrackID column
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedObject", new TrackDto(0, userId, description, duration, date)); // 0 because we can retrieve id only from db, here exception is throwing => record not created => user doesn't have id
            throw new DAOException(exceptionsConfig.getString("createFail"), e);
        } finally {
            close(statement, connection);
        }
    }

    public TrackDto retrieveTrackDto(int id) throws DAOException {
        if (id <= 0) {
            exceptionsConfig.setProperty("failedParameter", "Track Id");
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;
        String getTrackStatement;

        try {
            getTrackStatement = statementsConfig.getString("getTracksById");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(getTrackStatement);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            resultSet.next();
            TrackDto trackDto = new TrackDto(
                    resultSet.getInt(TRACK_ID.getColumnName()),
                    resultSet.getInt(USER_ID.getColumnName()),
                    resultSet.getString(DESCRIPTION.getColumnName()),
                    Duration.parse(resultSet.getString(DURATION.getColumnName())),
                    LocalDate.parse(resultSet.getString(DATE.getColumnName()))
            );
            logger.debug(loggerMessages.getString("trackDtoReturned") + trackDto);
            return trackDto;
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedParameter", id);
            throw new DAOException(exceptionsConfig.getString("retrieveTrackFail"), e);
        } finally {
            close(resultSet, statement, connection);
        }
    }

    public List<TrackDto> retrieveTracksDtoByUserId(int id) throws DAOException {
        if (id <= 0) {
            exceptionsConfig.setProperty("failedParameter", "Track Id");
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;
        String getTracksStatement;

        try {
            getTracksStatement = statementsConfig.getString("getTracksByUserId");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(getTracksStatement);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            TrackDto trackDto;
            List<TrackDto> tracksDto = new ArrayList<>();
            logger.info(loggerMessages.getString("trackDtoFind") + tracksDto);
            while (resultSet.next()) {
                trackDto = new TrackDto(
                        resultSet.getInt(TRACK_ID.getColumnName()),
                        resultSet.getInt(USER_ID.getColumnName()),
                        resultSet.getString(DESCRIPTION.getColumnName()),
                        Duration.parse(resultSet.getString(DURATION.getColumnName())),
                        LocalDate.parse(resultSet.getString(DATE.getColumnName()))
                );
                tracksDto.add(trackDto);
                logger.debug(loggerMessages.getString("trackDtoReturned"));
            }
            return tracksDto;
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedParameter", id);
            throw new DAOException(exceptionsConfig.getString("retrieveTracksFail"), e);
        } finally {
            close(resultSet, statement, connection);
        }
    }
}
