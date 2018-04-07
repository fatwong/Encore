package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.Song;

import java.util.List;

/**
 * Created by Isaac on 2018/3/7.
 */

public interface LocalIView {

    interface LocalMusic{
        void getLocalMusicSuccess(List<Song> songs);
        void getLocalMusicFailed(Throwable throwable);
    }

    interface LocalAlbum{

    }
    interface LocalArtist{

    }

}
