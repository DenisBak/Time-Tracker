package com.denis.control;

import com.denis.domain.Track;
import com.denis.domain.User;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.exceptions.NegativeDurationException;
import com.denis.domain.factories.ConfigFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.denis.control.RequestParameters.*;
import static com.denis.control.RequestParameters.DATE;

public class UserController {
    private Map<User, List<Track>> tracks = new HashMap<>();
    private static UserController instance;
    private static Logger logger;
    private static Configuration exceptionConfig;

    private UserController() {
        logger = LogManager.getLogger();
        exceptionConfig = ConfigFactory.getConfigByName("exceptions");
    }

    public static UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    public void createTrack(User user, HttpServletRequest req) throws DomainException {
        try {
            Track t = Track.createTrack(
                    user.getId(), getParameter(DESCRIPTION, req), getParameter(START_TIME, req),
                    getParameter(END_TIME, req), getParameter(DATE, req)
            );
            user.addTrack(t);
            logger.info("track was created and added to user: " + user + "; track - " + t.getStringRepresentation());

        } catch (DomainException e) {
            logger.error(e);
            String message;
            if (e instanceof NegativeDurationException) message = exceptionConfig.getString("durationIsNegative");
            else message = exceptionConfig.getString("createTrackFail");
            throw new DomainException(message);
        }
    }

    private String getParameter(RequestParameters name, HttpServletRequest req) {
        Map<String, String[]> parameters = req.getParameterMap();
        return parameters.get(name.getName())[0];
    }
}
