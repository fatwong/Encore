package com.fatwong.encore.bean;

public class PlaylistRelation {

    private int id;
    private int playlistId;
    private int songId;

    public PlaylistRelation(int id, int playlistId, int songId) {
        this.id = id;
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }
}
