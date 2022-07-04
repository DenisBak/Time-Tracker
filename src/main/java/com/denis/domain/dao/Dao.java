package com.denis.domain.dao;

import com.denis.domain.exceptions.DAOException;
import com.denis.domain.configs.ConfigFactory;
import com.denis.domain.configs.ConfigNames;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class Dao {
    protected static Logger logger;
    protected static Configuration exceptionsConfig;
    protected static Configuration statementsConfig;
    protected static Configuration loggerMessages;

    protected Connection connection;

    static {
        logger = LogManager.getLogger();
        exceptionsConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);
        statementsConfig = ConfigFactory.getConfigByName(ConfigNames.STATEMENTS);
        loggerMessages = ConfigFactory.getConfigByName(ConfigNames.LOGGER_MESSAGES);
    }

    public void close(Statement statement, Connection connection) {
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

    public void close(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error(exceptionsConfig.getString("closeResultSetFail"), new DAOException(e));
        }

        close(statement, connection);
    }
}
