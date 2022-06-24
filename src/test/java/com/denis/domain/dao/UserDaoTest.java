package com.denis.domain.dao;

import com.denis.domain.User;
import com.denis.domain.dao.user.UserDao;
import com.denis.domain.dao.user.UserDto;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

//import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class UserDaoTest {
    UserDao dao = UserDao.getInstance();
    User alreadyCreatedUser;

    @Test
    public void getInstanceTest() {
        assertNotNull(dao);
    }

    @Test()
    public void createTest_Exceptions() {
        assertThrows(DAOException.class,
                () -> dao.createUser(null)
        );
        assertThrows(DomainException.class,
                () -> alreadyCreatedUser = User.createUser("login", "pass", "Denis")
        );
        assertNull(alreadyCreatedUser);
    }

    @Test
    public void retrieveIdTest() throws DAOException {
        assertThrows(DAOException.class,
                () -> dao.retrieveId("no such username")
        );
        assertThrows(DAOException.class,
                () -> dao.retrieveId(null)
        );
        assertThrows(DAOException.class,
                () -> dao.retrieveId("no such username"));
        assertEquals(1, dao.retrieveId("login"));
    }

    @Test
    public void retrieveUserDtoTest() {
        String exceptionMessage = null;
        try {
            dao.retrieveUserDto(null, "pass");
        } catch (DAOException e) {
            exceptionMessage = e.getMessage();
        }
        MatcherAssert.assertThat(exceptionMessage, containsString("Username can't be null"));

        try {
            dao.retrieveUserDto("username", null);
        } catch (DAOException e) {
            exceptionMessage = e.getMessage();
        }
        MatcherAssert.assertThat(exceptionMessage, containsString("Password can't be null"));

        try {
            UserDto userDto = dao.retrieveUserDto("login", "pass");
            assertEquals(1, userDto.getId());
            assertEquals("Denis", userDto.getName());
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }
}
