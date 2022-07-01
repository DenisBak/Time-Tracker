package com.denis.view;

import com.denis.control.Protector;
import com.denis.control.RequestParameters;
import com.denis.control.UserController;
import com.denis.domain.Track;
import com.denis.domain.User;
import com.denis.domain.exceptions.ControlException;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.factories.ConfigFactory;
import com.denis.domain.factories.ConfigNames;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.denis.control.RequestParameters.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Workspace extends HttpServlet {
    private Logger logger;
    private Protector protector;
    private UserController userController;
    private Configuration exceptionConfig;
    private User user;

    public Workspace() {
        logger = LogManager.getLogger();
        protector = Protector.getInstance();
        exceptionConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);
        userController = UserController.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");

        try {
            user = protector.checkUserAuthorization(req);
            logger.info("working with user: " + user);
        } catch (ControlException e) {
            logger.error(e.getMessage(), e);

            resp.sendRedirect("/timeTracker/login");
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + exceptionConfig.getString("userNotLoggedIn") +"</h3>"
            ); // TODO: 6/15/22 message doesn't see think about how to include message in redirecting html
        }

        req.getRequestDispatcher("/links.html").include(req, resp);
        out.println("<h1>Welcome back, " + user.getName() + "!</h1>");
        req.getRequestDispatcher("/timeForm.html").include(req, resp);

        List<Track> tracks = user.getTracks();
        logger.info("Tracks was returned " + tracks);
        out.println("<h2>Your tracks:</h2>");

        for (Track track : tracks) {
            out.println("<h3>" + track.getStringRepresentation() + "</h3>");
            logger.info("Printed track " + track);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");

        try {
            userController.createTrack(user, req);
            resp.sendRedirect("/timeTracker/workspace");
        } catch (DomainException e) {
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + e.getMessage() +"</h3>"
            ); // TODO: 6/15/22 message doesn't see think about how to include message in redirecting html
        }
    }
    // TODO: 6/30/22 when start time and end time is none (in chrome form) than stack trace looked. Replace it on face exception
}
