package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.SongInfo;

public interface ICallback {

    void getSongInfoSuccess(SongInfo songInfo);

    void getSongInfoFailed(Throwable e);

}
