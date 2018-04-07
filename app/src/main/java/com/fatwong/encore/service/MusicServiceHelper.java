package com.fatwong.encore.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Isaac on 2018/3/23.
 */

public class MusicServiceHelper {
    private Context mContext;

    private static MusicServiceHelper musicServiceHelper = new MusicServiceHelper();

    public static MusicServiceHelper get(Context context) {
        musicServiceHelper.mContext = context;
        return musicServiceHelper;
    }

    MusicService musicService;

    public void initService() {
        if (musicService == null) {
            Intent intent = new Intent(mContext, MusicService.class);
            ServiceConnection connection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName componentName, IBinder service) {
                    MusicService.MyBinder binder = (MusicService.MyBinder) service;
                    musicService = binder.getMusicService();
                    musicService.setUp();
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
            mContext.startService(intent);
            mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }
}
