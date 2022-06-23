package com.denis.view;

import com.denis.control.Protector;
import com.denis.domain.User;
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

public class Workspace extends HttpServlet {
    private Logger logger;
    private Protector protector;
    private Configuration exceptionConfig;
    private User user;

    public Workspace() {
        logger = LogManager.getLogger();
        protector = Protector.getInstance();
        exceptionConfig = ConfigFactory.getConfigByName("exceptions");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user;
        try {
            user = protector.checkUserAuthorization(req);
            this.user = user;

            resp.setContentType("text/html");
            req.getRequestDispatcher("/links.html").include(req, resp);
            req.getRequestDispatcher("/timeForm.html").include(req, resp);
        } catch (ControlException e) {
            PrintWriter out = resp.getWriter();

            logger.error(e.getMessage(), e);

            resp.sendRedirect("/timeTracker/login");
            out.println(
                    "<h3 style=\"color: red; text-align: center;\">" + exceptionConfig.getString("userNotLoggedIn") +"</h3>"
            ); // TODO: 6/15/22 message doesn't see think about how to include message in redirecting html
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
