package com.fatwong.encore.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.fatwong.encore.bean.RecentPlaylist;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnSongChangeListener;

public class MusicService extends Service implements OnSongChangeListener {

    public MusicService() {
    }

    public final Binder mBinder = new MyBinder();

    private MediaSessionCompat mediaSession;
    private MusicPlayerManager musicPlayerManager;
    private PlaybackStateCompat mState;

    @Override
    public void onSongChanged(Song song) {
        RecentPlaylist.getInstance().addPlayedSong(song);
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

    }

    public class MyBinder extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    /**
     * 服务入口方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void setUp() {
        musicPlayerManager = MusicPlayerManager.from(this);
        MusicPlayerManager.get().registerOnSongChangedListener(this);
        setUpMediaSession();
    }

    /**
     *
     */
    private void setUpMediaSession() {
        ComponentName componentName = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
        mediaSession = new MediaSessionCompat(this, "fd", componentName, null);
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSessionCallback());
        setState(PlaybackStateCompat.STATE_NONE);
    }

    /**
     * 设置播放状态
     * @param state 播放状态
     */
    public void setState(int state) {
        mState = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackStateCompat.ACTION_SEEK_TO
                        | PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
                        | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH)
                .setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1.0f, SystemClock.elapsedRealtime())
                .build();
        mediaSession.setPlaybackState(mState);
        mediaSession.setActive(state != PlaybackStateCompat.STATE_NONE && state != PlaybackStateCompat.STATE_STOPPED);
    }

    /**
     * 获取播放状态
     * @return 播放状态
     */
    public int getState() {
        return mState.getState();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }

    public void stopService() {
        stopSelf();
    }

    class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }
    }
}
