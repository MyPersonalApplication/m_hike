package com.example.m_hike.model;

public class Photo {
    private static long lastAssignedId = 0;
    private long id;
    private final long observationId;
    private String title;
    private String description;
    private byte[] imageUrl;
    private final String timestamp;

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

    public static void setLastAssignedId(long lastAssignedId) {
        Photo.lastAssignedId = lastAssignedId;
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
}
