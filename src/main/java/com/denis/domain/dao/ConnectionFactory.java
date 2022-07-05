package com.denis.domain.dao;

import com.denis.domain.exceptions.DAOException;
import com.denis.domain.configs.ConfigFactory;
import com.denis.domain.configs.ConfigNames;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private static Configuration dbConfig;
    private static Configuration exceptionsConfig;

    private static DataSource dataSource;

    private static Logger logger;

    static {
        MysqlDataSource mysqlDS = new MysqlDataSource();
        logger = LogManager.getLogger();
        
        dbConfig = ConfigFactory.getConfigByName(ConfigNames.DB);
        exceptionsConfig = ConfigFactory.getConfigByName(ConfigNames.EXCEPTIONS);

        mysqlDS.setURL(dbConfig.getString("url") + dbConfig.getString("name"));
        mysqlDS.setUser(dbConfig.getString("username"));
        mysqlDS.setPassword(dbConfig.getString("password"));

        dataSource = mysqlDS;
    }

    public static Connection getConnection() throws DAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            DAOException ex = new DAOException(exceptionsConfig.getString("connectionFail"), e);
            logger.error(ex);
            throw ex;
        }
    }
}
