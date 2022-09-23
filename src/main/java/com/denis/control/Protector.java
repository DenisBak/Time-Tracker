package com.denis.control;

import com.denis.control.password.PasswordCheck;
import com.denis.domain.User;
import com.denis.domain.exceptions.ControlException;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.configs.ConfigFactory;
import com.denis.domain.configs.ConfigNames;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import static com.denis.control.RequestParameters.*;

public class Protector {
    private static final int SIX_HOURS = 60 * 60 * 6; // In seconds

    private static Protector instance;
    private static Logger logger;

    private static final Configuration exceptionConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);
    private static final Configuration logMessages = ConfigFactory.getConfigByName(ConfigNames.LOGGER_MESSAGES);

    private static Map<TemporaryCookie, User> authorizedUsers = new HashMap<>();

    private final String sidCookieName = "SID";

    private Protector() {
        logger = LogManager.getLogger();
    }

    public static Protector getInstance() {
        if (instance == null) {
            instance = new Protector();
        }
        return instance;
    }

    public void loginUser(HttpServletRequest req, HttpServletResponse resp) throws ControlException {
        String username = req.getParameter(USERNAME.getName());
        String password = req.getParameter(PASSWORD.getName());
        logger.info(logMessages.getString("startLogin") + username + ", password: " + password);
        try {
            User user = User.getUser(username, password);

            authorizeUser(user, resp);
        } catch (DomainException e) {
            throw new ControlException(e);
        }
    }

    public void registerUser(HttpServletRequest req, HttpServletResponse resp) throws ControlException {
        String username = req.getParameter(USERNAME.getName());
        String firstPassword = req.getParameter(FIRST_PASSWORD_REG.getName());
        String secondPassword = req.getParameter(SECOND_PASSWORD_REG.getName());
        String name = req.getParameter(NAME.getName());

        logger.info(logMessages.getString("registerUser") + username + " " + firstPassword + " " + secondPassword + " " + name);
        validatePasswords(firstPassword, secondPassword);
        try {
            User user = User.createUser(username, firstPassword, name);

            authorizeUser(user, resp);
        } catch (DomainException e) {
            throw new ControlException(exceptionConfig.getString("usernameAlreadyTaken"), e);
        }
    }

    public User checkUserAuthorization(HttpServletRequest req) throws ControlException {
        logger.info(logMessages.getString("startChecking"));
        TemporaryCookie cookie = getTemporaryCookieByName(sidCookieName, req);

        if (cookie.isValid()) {
            return authorizedUsers.get(cookie);
        } else {
            exceptionConfig.setProperty("failedCookie", cookie);
            ControlException e = new ControlException(exceptionConfig.getString("userNotAuthorized"));
            logger.error(e);
            throw e;
        }
    }

    public void logoutUser(HttpServletRequest req, HttpServletResponse resp) {
        TemporaryCookie cookie = getTemporaryCookieByName(sidCookieName, req);
        Cookie cookieSid = cookie.getCookie();
        cookieSid.setMaxAge(0);
        resp.addCookie(cookieSid);
        cookie.removeCookie();
    }

    private void authorizeUser(User user, HttpServletResponse resp) {
        String sidValue = generateEncryptedValue(user.getUsername());
        TemporaryCookie cookie = TemporaryCookie.of(sidCookieName, sidValue, SIX_HOURS);
        resp.addCookie(cookie.getCookie());
        authorizedUsers.put(cookie, user);
        logger.info("authorized was successful " + cookie + " " + user);
    }

    private boolean validatePasswords(String firstPassword, String secondPassword) throws ControlException {
        return PasswordCheck.run(firstPassword, secondPassword);
    }

    private TemporaryCookie getTemporaryCookieByName(String name, HttpServletRequest req) {
        String cookieValue = "";
        logger.info("start find temporary cookie by name " + name);
        if (req.getCookies() == null) return TemporaryCookie.get(cookieValue);
        for (Cookie cookie : req.getCookies()) {
            if (cookie.getName().equals(name)) {
                logger.info(logMessages.getString("cookieFind") + cookie.getName() + ", " + cookie.getValue());
                cookieValue = cookie.getValue();
            }
        }
        return TemporaryCookie.get(cookieValue);
    }

    private String generateEncryptedValue(String valueToEncrypt) {
        byte[] value = valueToEncrypt.getBytes(StandardCharsets.UTF_8);
        int randomNonceSize = 20;
        byte[] rawNonce = getRandomNonce(randomNonceSize);

        byte[] nonce = ByteBuffer.allocate(value.length + rawNonce.length)
                .put(rawNonce)
                .put(value)
                .array();

        return DigestUtils.sha256Hex(nonce);
    }

    private byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }


    private static class TemporaryCookie {
        private static Set<TemporaryCookie> activeCookies;
        private final Cookie cookie;
        private final LocalDateTime expiredTime;
        private static TemporaryCookie invalid;

        static {
            activeCookies = authorizedUsers.keySet();
            new Thread(new RemoveInactiveCookie()).start();
        }

        private TemporaryCookie(String name, String value, int seconds) {
            this.cookie = new Cookie(name, value);
            this.cookie.setMaxAge(seconds);
            this.expiredTime = LocalDateTime.now().plusSeconds(seconds);
//            activeCookies.add(this);
        }

        public static TemporaryCookie of(String name, String value, int seconds) {
            TemporaryCookie result = get(value);
            if (result.isValid()) {
                return result;
            }
            return new TemporaryCookie(name, value, seconds);
        }

        public static TemporaryCookie get(String value) {

            logger.info("start finding cookie by value " + value);
            for (TemporaryCookie cookie : activeCookies) {
                if (cookie.getCookie().getValue().equals(value)) { // TODO: 7/15/22 find cookie
                    logger.info("find " + value);
                    return cookie;
                }
            }

            logger.info("can't find, return " + getInvalid());
            return getInvalid();
        }

        private static class RemoveInactiveCookie implements Runnable {
            @Override
            public void run() {
                logger.info("start checking inactive cookies");
                while (activeCookies != null) {
                    for (TemporaryCookie tc : activeCookies) {
                        if (!tc.isValid()) {
                            tc.removeCookie();
                            break;
                        }
                    }
                }
            }
        }

        public void removeCookie() {
            activeCookies.remove(this);
            logger.info(logMessages.getString("cookieWasRemoved") + this);
        }

        public Cookie getCookie() {
            return cookie;
        }

        public LocalDateTime getExpiredTime() {
            return expiredTime;
        }

        public boolean isValid() {
            return this.getExpiredTime().compareTo(LocalDateTime.now()) > 0;
        }

        private static TemporaryCookie getInvalid() {
            if (invalid == null) {
                invalid = new TemporaryCookie("invalid", "invalid", 0);
            }
            return invalid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TemporaryCookie cookie1 = (TemporaryCookie) o;
            return cookie.getValue().equals(cookie1.cookie.getValue()) && expiredTime.equals(cookie1.expiredTime);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cookie.getValue(), expiredTime);
        }

        @Override
        public String toString() {
            return "TemporaryCookie{" +
                    "cookie name = " + cookie.getName() +
                    ", cookie value = " + cookie.getValue() +
                    ", expiredTime = " + expiredTime +
                    '}';
        }
    }
}
