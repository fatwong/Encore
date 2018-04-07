package com.fatwong.encore.bean;

import com.google.gson.annotations.SerializedName;

public class AlbumQuery {

    private static final String ALBUM_NAME = "album";
    private static final String ARTIST_NAME = "artist";

    @SerializedName(ALBUM_NAME)
    public String mAlbum;

    @SerializedName(ARTIST_NAME)
    public String mArtist;

    public AlbumQuery(String album, String artist) {
        this.mAlbum = album;
        this.mArtist = artist;
    }

}
