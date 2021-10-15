package com.tud.database.models;

public class User {
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private String username;
    private String password;

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
