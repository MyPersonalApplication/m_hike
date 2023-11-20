package com.example.m_hike.model;

import java.io.Serializable;

public class Observation implements Serializable {
    private static long lastAssignedId = 0;
    private long id;
    private final long hikeId;
    private String name;
    private String time;
    private String additionalComment;

    public Observation(long hikeId, String name, String time, String additionalComment) {
        this.id = ++lastAssignedId;
        this.hikeId = hikeId;
        this.name = name;
        this.time = time;
        this.additionalComment = additionalComment;
    }

    public Observation(long id, long hikeId, String name, String time, String additionalComment) {
        this.id = id;
        this.hikeId = hikeId;
        this.name = name;
        this.time = time;
        this.additionalComment = additionalComment;
    }

    public static void setLastAssignedId(long lastAssignedId) {
        Observation.lastAssignedId = lastAssignedId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHikeId() {
        return hikeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAdditionalComment() {
        return additionalComment;
    }

    public void setAdditionalComment(String additionalComment) {
        this.additionalComment = additionalComment;
    }
}
