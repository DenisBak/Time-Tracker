package com.denis.view;

import com.denis.control.Protector;
import com.denis.domain.exceptions.ControlException;
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

public class Registration extends HttpServlet {
    private Logger logger;
    private Protector protector;

    private static final Configuration loggerMessages = ConfigFactory.getConfigByName(ConfigNames.LOGGER_MESSAGES);

    public Registration() {
        logger = LogManager.getLogger();
        protector = Protector.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.getRequestDispatcher("/links.html").include(req, resp);
        try {
            protector.checkUserAuthorization(req);
            logger.debug(loggerMessages.getString("redirectWorkspace"));
            resp.sendRedirect("/timeTracker/workspace");
        } catch (ControlException e) {
            logger.error(e.getMessage(), e);
            req.getRequestDispatcher("/registration.html").include(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        try {
            protector.registerUser(req, resp);
            resp.sendRedirect("/timeTracker/workspace");
        } catch (ControlException e) {
            PrintWriter out = resp.getWriter();

            logger.error(e.getMessage(), e);

            req.getRequestDispatcher("/links.html").include(req, resp);
            req.getRequestDispatcher("/registration.html").include(req, resp);
            out.println("<br>");
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + e.getMessage() +"</h3>"
            );
        }
    }
}
