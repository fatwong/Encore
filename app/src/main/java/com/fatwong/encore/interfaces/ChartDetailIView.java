package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.ChartInfo;

public interface ChartDetailIView {

    void loadMusicDetailData(ChartInfo chartInfoBean);
    void loadFail(Throwable e);

}
