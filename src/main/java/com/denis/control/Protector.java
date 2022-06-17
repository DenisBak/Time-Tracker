package com.denis.control;

import com.denis.domain.User;
import com.denis.domain.exceptions.ControlException;
import com.denis.domain.exceptions.DomainException;
import com.denis.domain.factories.ConfigFactory;
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
    private static Protector instance;
    private static Logger logger;
    private static Configuration exceptionConfig;

    private Map<String, User> authorizedUsers = new HashMap<>();

    private String sidCookieName = "SID";
    private int randomNonceSize = 20;

    private Protector() {
        logger = LogManager.getLogger();
        exceptionConfig = ConfigFactory.getConfigByName("exceptions");
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
        logger.info("Was retrieved username: " + username + ", password: " + password);
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

        logger.info("register this user : " + username + " " + firstPassword + " " + secondPassword + " " + name);
        validatePasswords(firstPassword, secondPassword);
        try {
            User user = User.createUser(username, firstPassword, name);

            authorizedUser(user, resp);
        } catch (DomainException e) {
            throw new ControlException(exceptionConfig.getString("usernameAlreadyTaken"));
        }
    }

    public User checkUserAuthorization(HttpServletRequest req) throws ControlException {
        logger.info("start checking user authorization");
        String cookieSid = getCookieValueByName(req, sidCookieName);
        User user = authorizedUsers.get(cookieSid);
        logger.info("cookie - " + cookieSid + " user - " + user);
        if (user == null) {
            logger.info("User is not registered"); // TODO: 6/15/22 create logger messages
            exceptionConfig.setProperty("failedCookieValue", cookieSid);
            throw new ControlException(exceptionConfig.getString("noSuchUser"));
        }
        logger.info("User is registered");
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
                logger.info("successfully find cookie - "+ cookie.getName() + ", " + cookie.getValue());
                return cookie;
            }
        }
        exceptionConfig.setProperty("failedCookieName", name);
        logger.error(exceptionConfig.getString("noSuchCookie"));
        return new Cookie(name, "");
    }

    private void setSidCookie(String cookieValue, HttpServletResponse resp) {
        Cookie sidCookie = new Cookie(sidCookieName, cookieValue); // TODO: 6/2/22 set live time of cookies
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
