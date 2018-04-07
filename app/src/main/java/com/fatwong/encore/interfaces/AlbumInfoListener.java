package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.LastfmAlbum;

public interface AlbumInfoListener {

    void albumInfoSuccess(LastfmAlbum album);

    void albumInfoFailed();

}