package com.example.m_hike.model;

import java.io.Serializable;
import java.util.Date;

public class Hike implements Serializable {
    private static int lastAssignedId = 0;
    private long id;
    private String username;
    private String name;
    private String location;
    private Float latitude;
    private Float longitude;
    private String date;
    private String parkingAvailable;
    private Float length;
    private String difficultyLevel;
    private String description;

    public Hike(String username, String name, String location, Float latitude, Float longitude, String date, String parkingAvailable, Float length, String difficultyLevel, String description) {
        this.id = ++lastAssignedId;
        this.username = username;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficultyLevel = difficultyLevel;
        this.description = description;
    }

    public Hike(long id, String username, String name, String location, Float latitude, Float longitude, String date, String parkingAvailable, Float length, String difficultyLevel, String description) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficultyLevel = difficultyLevel;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(String parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
