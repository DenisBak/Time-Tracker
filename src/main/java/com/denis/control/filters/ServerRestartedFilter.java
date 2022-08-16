//package com.denis.control.filters;
//
//import com.denis.control.Protector;
//import com.denis.domain.configs.ConfigFactory;
//import com.denis.domain.configs.ConfigNames;
//import jakarta.servlet.*;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.apache.commons.configuration2.Configuration;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//public class ServerRestartedFilter implements Filter {
//    public static final Path restartFlag = Path.of(System.getProperty("user.home"), "serverRestarted");
//
//    private static Protector protector = Protector.getInstance();
//    private static boolean serverRestarted;
//    private static final Logger logger = LogManager.getLogger();
//    private static final Configuration logMessages = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);
//
//    static {
//        try {
//            serverRestarted = Files.deleteIfExists(restartFlag);
//        } catch (IOException e) {
//            logger.error(e);
//        }
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//
//        if (serverRestarted) {
//            protector.logoutUser(request, response);
//            logger.info(logMessages.getString("serverRestarted"));
//            serverRestarted = false;
//        }
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//}
