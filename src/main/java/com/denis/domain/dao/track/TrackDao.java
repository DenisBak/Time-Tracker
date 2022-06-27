package com.denis.domain.dao.track;

import com.denis.domain.dao.ConnectionFactory;
import com.denis.domain.dao.user.UserDto;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.Track;
import com.denis.domain.factories.ConfigFactory;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;

public class TrackDao {
    private static TrackDao instance;

    private static Configuration exceptionsConfig;
    private static Configuration statementsConfig;

    private Connection connection;

    private static Logger logger;

    private TrackDao() {
        logger = LogManager.getLogger();
        exceptionsConfig = ConfigFactory.getConfigByName("exceptions");
        statementsConfig = ConfigFactory.getConfigByName("statements");
    }

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
            if (userId <= 0)
                exceptionsConfig.setProperty("failedParameter", "User Id"); // TODO: 5/31/22 make it in normal
            else if (description == null) exceptionsConfig.setProperty("failedParameter", "Description");
            else if (duration == null) exceptionsConfig.setProperty("failedParameter", "Duration");
            else exceptionsConfig.setProperty("failedParameter", "Date");
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        String createTrackStatement;
        PreparedStatement statement = null;

        try {
            createTrackStatement = statementsConfig.getString("createTrack");
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(createTrackStatement, Statement.RETURN_GENERATED_KEYS); // TODO: 6/24/22 think about creating general method for all createSomething() via preparedStatement(statement, String[] columnNames);

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
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                logger.error(exceptionsConfig.getString("closeStatementFail"), new DAOException(e));
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error(exceptionsConfig.getString("closeConnectionFail"), new DAOException(e));
            }
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
            getTrackStatement = statementsConfig.getString("getTrackById");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(getTrackStatement);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            resultSet.next();
            TrackDto trackDto = new TrackDto(
                    resultSet.getInt("TrackID"), // TODO: 6/27/22 CREATE ENUM FOR COLUMN NAMES
                    resultSet.getInt("UserID"),
                    resultSet.getString("Description"),
                    Duration.parse(resultSet.getString("Duration")),
                    LocalDate.parse(resultSet.getString("Date"))
            );
            logger.debug("Was returned " + trackDto);
            return trackDto;
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedParameter", id);
            throw new DAOException(exceptionsConfig.getString("retrieveTrackFail"), e);
        } finally { // TODO: 6/27/22 think about creating general method close(ResultSet, Statement, Connection);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                logger.error(exceptionsConfig.getString("closeResultSetFail"), new DAOException(e));
            }

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                logger.error(exceptionsConfig.getString("closeStatementFail"), new DAOException(e));
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error(exceptionsConfig.getString("closeConnectionFail"), new DAOException(e));
            }
        }
    }
}
