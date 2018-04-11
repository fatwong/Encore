package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.OnlinePlaylistInfo;

public interface OnlinePlaylistDetailIView {

    void loadMusicDetailData(OnlinePlaylistInfo baseBean);
    void loadFail(Throwable e);

}
