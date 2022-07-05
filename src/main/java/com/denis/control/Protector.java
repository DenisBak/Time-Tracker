package com.denis.control;

import com.denis.control.password.PasswordCheck;
import com.denis.domain.User;
import com.denis.domain.exceptions.ControlException;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.configs.ConfigFactory;
import com.denis.domain.configs.ConfigNames;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static com.denis.control.RequestParameters.*;

public class Protector {
    private static final int SIX_HOURS = 60 * 60 * 6;

    private static Protector instance;
    private static Logger logger;

    private static final Configuration exceptionConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);
    private static final Configuration loggerMessages = ConfigFactory.getConfigByName(ConfigNames.LOGGER_MESSAGES);

    private Map<String, User> authorizedUsers = new HashMap<>();

    private final String sidCookieName = "SID";
    private final int randomNonceSize = 20;

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
        logger.info(loggerMessages.getString("startLogin") + username + ", password: " + password);
        try {
            User user = User.getUser(username, password);

            authorizedUser(user, resp);
        } catch (DomainException e) {
            throw new ControlException(e);
        }
    }

    // TODO: 6/6/22 подумай над тем что, когда ты выключаешь сервер, сиды на сервере пропадают, а куки остаются и при дальнейшем сравнении (после выключения)
    // TODO: 6/6/22 он выдает ошибку т.к. сравнивает куки с сервера с куки в браузере -> они не совпадают -> пользователь не зареган
    // TODO: 6/15/22 может быть просто сделать админскую панель в которой я перед перезагрузкой буду сетить опредленный флаг, сигнализирующий о том, что сервак был перезапущен и где то в одноим месте буду проверять если так то удалить куки
    // TODO: 6/15/22 либо связать куки и сессию сервера, проверка такая же как описано выше

    public void registerUser(HttpServletRequest req, HttpServletResponse resp) throws ControlException {
        String username = req.getParameter(USERNAME.getName());
        String firstPassword = req.getParameter(FIRST_PASSWORD_REG.getName());
        String secondPassword = req.getParameter(SECOND_PASSWORD_REG.getName());
        String name = req.getParameter(NAME.getName());

        logger.info(loggerMessages.getString("registerUser") + username + " " + firstPassword + " " + secondPassword + " " + name);
        validatePasswords(firstPassword, secondPassword);
        try {
            User user = User.createUser(username, firstPassword, name);

            authorizedUser(user, resp);
        } catch (DomainException e) {
            throw new ControlException(exceptionConfig.getString("usernameAlreadyTaken"));
        }
    }

    public User checkUserAuthorization(HttpServletRequest req) throws ControlException {
        logger.info(loggerMessages.getString("startChecking"));
        String cookieSid = getCookieValueByName(req, sidCookieName);
        User user = authorizedUsers.get(cookieSid);
        if (user == null) {
            exceptionConfig.setProperty("failedCookie", cookieSid);
            ControlException e = new ControlException(exceptionConfig.getString("userNotAuthorized"));
            logger.error(e);
            throw e;
        }
        return user;
    }

    public void logoutUser(HttpServletRequest req, HttpServletResponse resp) {
        Cookie cookieSid = getCookieByName(req, sidCookieName);
        authorizedUsers.remove(cookieSid.getValue());
        cookieSid.setMaxAge(0);
        resp.addCookie(cookieSid);
    }

    private void authorizedUser(User user, HttpServletResponse resp) {
        String sid = generateEncryptedValue(user.getUsername());

        authorizedUsers.put(sid, user);
        setSidCookie(sid, resp);
    }

    private boolean validatePasswords(String firstPassword, String secondPassword) throws ControlException {
        return PasswordCheck.run(firstPassword, secondPassword);
    }

    private String getCookieValueByName(HttpServletRequest req, String name) {
        return getCookieByName(req, name).getValue();
    }

    private Cookie getCookieByName(HttpServletRequest req, String name) {
        for (Cookie cookie : req.getCookies()) {
            if (cookie.getName().equals(name)) {
                logger.info(loggerMessages.getString("cookieFind") + cookie.getName() + ", " + cookie.getValue());
                return cookie;
            }
        }
        exceptionConfig.setProperty("failedCookieName", name);
        logger.error(exceptionConfig.getString("noSuchCookie"));
        return new Cookie(name, "");
    }

    private void setSidCookie(String cookieValue, HttpServletResponse resp) {
        Cookie sidCookie = new Cookie(sidCookieName, cookieValue);
        sidCookie.setMaxAge(SIX_HOURS);
        resp.addCookie(sidCookie);
    }

    private String generateEncryptedValue(String valueToEncrypt) {
        byte[] value = valueToEncrypt.getBytes(StandardCharsets.UTF_8);
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
}
