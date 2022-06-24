package com.denis.view;

import com.denis.control.Protector;
import com.denis.domain.exceptions.ControlException;
import com.denis.domain.factories.ConfigFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

public class Login extends HttpServlet {
    private Logger logger;
    private Protector protector;
    private Configuration exceptionConfig;

    public Login() {
        logger = LogManager.getLogger();
        protector = Protector.getInstance();
        exceptionConfig = ConfigFactory.getConfigByName("exceptions");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        req.getRequestDispatcher("/links.html").include(req, resp);
        try {
            protector.checkUserAuthorization(req);
            resp.sendRedirect("/timeTracker/workspace");
        } catch (ControlException e) {
            logger.error(e.getMessage(), e);
            req.getRequestDispatcher("/login.html").include(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        try {
            protector.loginUser(req, resp);
            resp.sendRedirect("/timeTracker/workspace");
        } catch (ControlException e) {
            PrintWriter out = resp.getWriter();

            logger.error(e.getMessage(), e);

            req.getRequestDispatcher("/links.html").include(req, resp);
            req.getRequestDispatcher("/login.html").include(req, resp);
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + exceptionConfig.getString("usernameOrPasswordIncorrect") +"</h3>"
            );
        }
    }
}