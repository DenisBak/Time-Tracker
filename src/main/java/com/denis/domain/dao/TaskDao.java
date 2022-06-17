package com.denis.domain.dao;

import com.denis.domain.exceptions.DAOException;
import com.denis.domain.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TaskDao {
    private static Properties exceptionProp;


    private Connection connection;
    private Logger logger = LogManager.getLogger();
    private String className = TaskDao.class.getName();

    public Task create(Task task) throws DAOException {
        connection = ConnectionFactory.getConnection();

        try {
            Statement statement = connection.createStatement();
        } catch (SQLException e) {
            DAOException ex = new DAOException(e);
            logger.error(
                    exceptionProp.getProperty("createTaskFail") , ex
            );
//            throw ex;
        }
        throw new NullPointerException("NOT REALIZED"); // TODO: 5/26/22 !!! not realized
    }
}