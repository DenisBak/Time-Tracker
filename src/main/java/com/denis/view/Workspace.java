package com.denis.view;

import com.denis.control.Protector;
import com.denis.control.UserController;
import com.denis.domain.Track;
import com.denis.domain.User;
import com.denis.domain.exceptions.ControlException;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.configs.ConfigFactory;
import com.denis.domain.configs.ConfigNames;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

            req.getRequestDispatcher("/links.html").include(req, resp);
            out.println("<h1>Welcome back, " + user.getName() + "!</h1>");
            req.getRequestDispatcher("/timeForm.html").include(req, resp);

            List<Track> tracks = user.getTracks();
            out.println("<h2>Your tracks:</h2>");

            for (Track track : tracks) {
                out.println("<h3>" + track.getStringRepresentation() + "</h3>");
            }
        } catch (ControlException e) {
            logger.error(e);

            req.getRequestDispatcher("/login").include(req, resp);
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + exceptionConfig.getString("userNotLoggedIn") +"</h3>"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");

        try {
            userController.createAndAddTrack(user, req);
            resp.sendRedirect("/timeTracker/workspace");
        } catch (DomainException e) {
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + e.getMessage() +"</h3>"
            );
            doGet(req, resp);
        }
    }
}
