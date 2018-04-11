package com.fatwong.encore.utils;

import com.fatwong.encore.bean.SongInfo;
import com.fatwong.encore.interfaces.ICallback;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

public class OkGoUtils {

    private static OkGoUtils instance;

    public static OkGoUtils getInstance() {
        if (instance == null)
            instance = new OkGoUtils();

        return instance;
    }

    public void getSongInfo(String songid, final ICallback callback) {
        OkGo.<String>get("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid="+songid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        SongInfo songInfo = gson.fromJson(response.body(), SongInfo.class);
                        callback.getSongInfoSuccess(songInfo);
                    }
                });

    }
}
