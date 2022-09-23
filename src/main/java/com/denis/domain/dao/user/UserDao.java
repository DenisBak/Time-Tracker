package com.denis.domain.dao.user;

import com.denis.domain.User;
import com.denis.domain.dao.ConnectionFactory;
import com.denis.domain.dao.Dao;
import com.denis.domain.exceptions.DAOException;

import static com.denis.domain.dao.ColumnNames.*;

import java.sql.*;

public class UserDao extends Dao {
    private static UserDao instance;

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    public void createUser(User user) throws DAOException {
        if (user == null) {
            exceptionsConfig.setProperty("failedParameter", "User");
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }
        createUser(user.getUsername(), user.getPassword(), user.getName());
    }

    public static void main(String[] args) {
        String createUserStatement;
        PreparedStatement statement = null;
        Connection connection;
        String username = "densdfsd";


        try {
            createUserStatement = statementsConfig.getString("createUser");
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(createUserStatement);

            statement.setString(1, username);
            String password = "FUCKYOUho228337!";
            statement.setString(2, password);
            String name = "den";
            statement.setString(3, name);

            statement.execute();
        } catch (DAOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(String username, String password, String name) throws DAOException {
        if (username == null || password == null || name == null) {
            String failedParam;
            if (username == null) failedParam = "Username";
            else if (password == null) failedParam = "Password";
            else failedParam = "Name";
            exceptionsConfig.setProperty("failedParameter", failedParam);
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        String createUserStatement;
        PreparedStatement statement = null;

        try {
            createUserStatement = statementsConfig.getString("createUser");
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(createUserStatement);

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, name);

            statement.execute();
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedObject", new UserDto(0, username, password, name)); // 0 because we can retrieve id only from db, here exception is throwing => record not created => user doesn't have id
            throw new DAOException(exceptionsConfig.getString("createFail"), e);
        } finally {
            close(statement, connection);
        }
    }

    public int retrieveId(String username) throws DAOException {
        if (username == null) {
            exceptionsConfig.setProperty("failedParameter", "Username");
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        ResultSet resultSet = null;
        PreparedStatement statement = null;
        String getIdStatement;

        try {
            getIdStatement = statementsConfig.getString("getUserId");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(getIdStatement);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            resultSet.next();
            return resultSet.getInt(USER_ID.getColumnName());
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedUsername", username);
            throw new DAOException(exceptionsConfig.getString("retrieveUserIdFail"), e);
        } finally {
            close(resultSet, statement, connection);
        }
    }

    public UserDto retrieveUserDto(String username, String password) throws DAOException {
        if (username == null || password == null) {
            String failedParam;
            if (username == null) failedParam = "Username";
            else failedParam = "Password";
            exceptionsConfig.setProperty("failedParameter", failedParam);
            throw new DAOException(new NullPointerException(
                    exceptionsConfig.getString("parameterNull")
            ));
        }

        ResultSet info = null;
        PreparedStatement statement = null;
        String getIdStatement;

        try {
            getIdStatement = statementsConfig.getString("getUserByUsernameAndPassword");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(getIdStatement);
            statement.setString(1, username);
            statement.setString(2, password);
            info = statement.executeQuery();

            info.next();
            UserDto userDto = new UserDto(
                    info.getInt(USER_ID.getColumnName()),
                    info.getString(USERNAME.getColumnName()),
                    info.getString(PASSWORD.getColumnName()),
                    info.getString(NAME.getColumnName())
            );
            logger.debug(loggerMessages.getString("userDtoReturned"));
            return userDto;
        } catch (SQLException e) {
            exceptionsConfig.setProperty("failedUsername", username);
            throw new DAOException(exceptionsConfig.getString("retrieveUserIdFail"), e);
        } finally {
            close(info, statement, connection);
        }
    }
}
