package com.denis.domain.dao.user;

import com.denis.domain.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDto {
    private int id;
    private String username;
    private String password;
    private String name;

    protected UserDto(int id, String username, String password, String name) {
        assert id >= 0;

        this.id = id;
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.name = Objects.requireNonNull(name);
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
