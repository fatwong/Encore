package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();

}
