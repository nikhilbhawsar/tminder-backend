package com.tminder.domain.model;

public class Media {
    private final String id;
    private final String title;

    public Media(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
}
