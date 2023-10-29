package com.example.m_hike.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String username;
    private String fullName;
    private String password;
    private byte[] avatar;
    private final String created;

    public User(String username, String fullName, String password, byte[] avatar, String created) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.avatar = avatar;
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getCreated() {
        return created;
    }
}
