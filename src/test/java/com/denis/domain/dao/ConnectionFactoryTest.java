package com.denis.domain.dao;

import com.denis.domain.exceptions.DAOException;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;

public class ConnectionFactoryTest {
    @Test
    public void getConnectionTest() throws DAOException {
        Connection connection = ConnectionFactory.getConnection();
        Assert.assertNotNull(connection);
    }
}
