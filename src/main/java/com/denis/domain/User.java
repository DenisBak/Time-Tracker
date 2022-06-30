package com.denis.domain;

import com.denis.domain.dao.user.UserDao;
import com.denis.domain.dao.user.UserDto;
import com.denis.domain.exceptions.DAOException;
import com.denis.domain.exceptions.DomainException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {
    private List<Track> tracks;

    private int id;
    private String username;
    private String password;
    private String name;

    private static UserDao dao = UserDao.getInstance();
    private static Logger logger = LogManager.getLogger();

    protected User(int id, String username, String password, String name) {
        setId(id);
        setUsername(username);
        setPassword(password);
        setName(name);
    }

    public static User createUser(String username, String password, String name) throws DomainException {
        User user;
        try {
            int id = dao.retrieveId(username);
            dao.createUser(username, password, name);
            user = new User(id, username, password, name);
        } catch (DAOException e) {
            throw new DomainException(e);
        }
        return user;
    }

    public static User getUser(String username, String password) throws DomainException {
        UserDto dto;
        User user;
        try {
            dto = dao.retrieveUserDto(username, password);
            logger.debug("DTO was retrieved " + dto);
            user = new User(dto.getId(), dto.getUsername(), dto.getPassword(), dto.getName()); // TODO: 6/29/22 create from dto constructor
            user.setTracks(Track.getTracksByUserId(dto.getId()));
            logger.debug("User was retrieved " + user);
        } catch (DAOException e) {
            throw new DomainException(e);
        }
        return user;
    }

    @Override
    public String toString() {
        return "User: " +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username) && password.equals(user.password);
    }

    public void addTrack(Track t) {
        assert t != null;
        tracks.add(t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Track> getTracks() {
        // TODO: 6/29/22 return new ArrayList(tracks)
        logger.info("user with id - " + id + " was returned tracks - " + tracks);
        return tracks;
    }
}
