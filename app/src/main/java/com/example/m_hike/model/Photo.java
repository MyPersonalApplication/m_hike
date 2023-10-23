package com.example.m_hike.model;

import java.util.Date;

public class Photo {
    private static int lastAssignedId = 0;
    private long id;
    private long observationId;
    private String title;
    private String description;
    private byte[] imageUrl;
    private String timestamp;

    public Photo(long observationId, String title, String description, byte[] imageUrl, String timestamp) {
        this.id = ++lastAssignedId;
        this.observationId = observationId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public Photo(long id, long observationId, String title, String description, byte[] imageUrl, String timestamp) {
        this.id = id;
        this.observationId = observationId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getObservationId() {
        return observationId;
    }

    public void setObservationId(long observationId) {
        this.observationId = observationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(byte[] imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
