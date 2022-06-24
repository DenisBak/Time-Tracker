package com.denis.domain.dao;

import com.denis.domain.exceptions.DAOException;
import com.denis.domain.Track;
import com.denis.domain.factories.ConfigFactory;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

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

//    public void createTrack(Track track) throws DAOException {
//        if (track == null) {
//            exceptionsConfig.setProperty("failedParameter", "User");
//            throw new DAOException(new NullPointerException(
//                    exceptionsConfig.getString("parameterNull")
//            ));
//        }
//        createUser(track.getUsername(), track.getPassword(), track.getName());
//    }
}