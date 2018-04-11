package com.fatwong.encore.presenter;

import android.content.Context;
import android.util.Log;

import com.fatwong.encore.bean.ChartInfo;
import com.fatwong.encore.bean.OnlinePlaylistInfo;
import com.fatwong.encore.interfaces.APIService;
import com.fatwong.encore.interfaces.ChartDetailIView;
import com.fatwong.encore.interfaces.OnlinePlaylistDetailIView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

public class MusicDetailPresenter {

    private Context mContext;
    private ChartDetailIView chartDetailIView;
    private OnlinePlaylistDetailIView onlinePlaylistDetailIView;

    public MusicDetailPresenter(Context context, ChartDetailIView chartDetailIView) {
        this.mContext = context;
        this.chartDetailIView = chartDetailIView;
    }

    public MusicDetailPresenter(Context context, OnlinePlaylistDetailIView onlinePlaylistDetailIView) {
        this.mContext = context;
        this.onlinePlaylistDetailIView = onlinePlaylistDetailIView;
    }

    public void initListData(int type, final String id) {
        switch (type) {
            case 0:
                OkGo.<String>get(APIService.PLAYLIST_INFO_URL + id)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Gson gson = new Gson();
                                OnlinePlaylistInfo onlinePlaylistInfo = gson.fromJson(response.body(), OnlinePlaylistInfo.class);
                                onlinePlaylistDetailIView.loadMusicDetailData(onlinePlaylistInfo);
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                onlinePlaylistDetailIView.loadFail(response.getException());
                            }
                        });
                break;
            case 1:
                OkGo.<String>get(APIService.BASE_PARAMETERS_URL + "rankinginside/" + id)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Gson gson = new Gson();
                                ChartInfo chartInfo = gson.fromJson(response.body(), ChartInfo.class);
                                chartDetailIView.loadMusicDetailData(chartInfo);
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                chartDetailIView.loadFail(response.getException());
                            }
                        });
                break;
        }
    }
}
