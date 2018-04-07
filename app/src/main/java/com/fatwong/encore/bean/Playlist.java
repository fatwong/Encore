package com.fatwong.encore.bean;

import java.io.Serializable;

public class Playlist implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String coverUrl;
    private int count;
    private String description;

    public Playlist() {
    }

    public Playlist(int id, String title, String coverUrl, int count, String description) {
        this.id = id;
        this.title = title;
        this.coverUrl = coverUrl;
        this.count = count;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
