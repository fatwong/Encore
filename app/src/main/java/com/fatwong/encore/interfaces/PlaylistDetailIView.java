package com.fatwong.encore.interfaces;

import android.graphics.Bitmap;
import android.text.Spanned;

import com.fatwong.encore.bean.Song;

import java.util.List;

public interface PlaylistDetailIView {

    void playlistDetail(int collectionId, Spanned title, Spanned description);

    void playlistCover(Bitmap cover);

    void getSongs(List<Song> songs);

    void fail(Throwable throwable);

}
