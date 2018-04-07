package com.fatwong.encore.interfaces;

import android.support.v4.media.session.PlaybackStateCompat;

import com.fatwong.encore.bean.Song;

/**
 * Created by Isaac on 2018/3/23.
 */

public interface OnSongChangeListener {
    void onSongChanged(Song song);

    void onPlayBackStateChanged(PlaybackStateCompat stateCompat);
}
