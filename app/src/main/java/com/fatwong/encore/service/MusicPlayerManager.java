package com.fatwong.encore.service;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;

import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.bean.Song;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 歌曲播放管理类
 * Created by Isaac on 2018/3/23.
 */

public class MusicPlayerManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener {
    private final static MusicPlayerManager instance = new MusicPlayerManager();

    private Context mContext;
    private MusicService musicService;
    private PlayQueue mPlayQueue;
    private MediaPlayer mediaPlayer;
    private long currentMediaId = -1;
    private int currentProgress;

    public static final int MAX_DURATION_FOR_REPEAT = 3000;
    private int currentMaxDuration = MAX_DURATION_FOR_REPEAT;

    private ArrayList<OnSongChangeListener> onSongChangeListeners = new ArrayList<>();

    public static void startServiceIfNecessary(Context context) {
        if (get().musicService == null) {
            MusicServiceHelper.get(context).initService();
        }
    }

    public static MusicPlayerManager get() {
        return instance;
    }

    public static MusicPlayerManager from(MusicService service) {
        return MusicPlayerManager.get().setContext(service).setService(service);
    }

    public MusicPlayerManager setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public MusicPlayerManager setService(MusicService service) {
        this.musicService = service;
        return this;
    }

    public void play() {
        if (mPlayQueue == null || mPlayQueue.getCurrentPlay() == null) {
            return;
        }
        play(mPlayQueue.getCurrentPlay());
    }

    public void playQueue(PlayQueue playQueue, int position) {
        mPlayQueue = playQueue;
        mPlayQueue.setCurrentPlay(position);
        play(mPlayQueue.getCurrentPlay());
    }

    public void playQueueItem(int position) {
        mPlayQueue.setCurrentPlay(position);
        play(mPlayQueue.getCurrentPlay());
    }

    public boolean isPlaying(){
        if(musicService.getState() == PlaybackStateCompat.STATE_PLAYING){
            return true;
        }
        return false;
    }

    private void play(Song song) {
        if (song == null) {
            return;
        }
        boolean musicHasChanged = !(song.getId() == currentMediaId);
        if (musicHasChanged) {
            currentProgress = 0;
            currentMediaId = song.getId();
        }
        if (musicService.getState() == PlaybackStateCompat.STATE_PAUSED && !musicHasChanged && mediaPlayer != null) {
            configMediaPlayerState();
        } else {
            createMediaPlayerIfNeeded();
            try {
                mediaPlayer.setDataSource(mContext, song.getUri());
                mediaPlayer.prepareAsync();

                for (OnSongChangeListener listener : onSongChangeListeners) {
                    listener.onSongChanged(song);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createMediaPlayerIfNeeded() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
        } else {
            mediaPlayer.reset();
        }
    }

    private void configMediaPlayerState() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (currentProgress == mediaPlayer.getCurrentPosition()) {
                mediaPlayer.start();
                musicService.setState(PlaybackStateCompat.STATE_PLAYING);
            } else {
                mediaPlayer.seekTo(currentProgress);
                mediaPlayer.start();
                musicService.setState(PlaybackStateCompat.STATE_PLAYING);
            }
        }
    }

    public void playNextSong() {
        currentProgress = 0;
        play(mPlayQueue.getNextSong());
    }

    public void playPreviousSong() {
        currentProgress = 0;
        play(mPlayQueue.getPreviousSong());
    }

    public void pause() {
        if (musicService.getState() == PlaybackStateCompat.STATE_PLAYING) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                currentProgress = mediaPlayer.getCurrentPosition();
            }
        }
        musicService.setState(PlaybackStateCompat.STATE_PAUSED);

    }

    public void resume() {
        if (musicService.getState() == PlaybackStateCompat.STATE_PAUSED && mediaPlayer != null) {
            mediaPlayer.start();
            musicService.setState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    public void clearPlayList() {
        if (getPlayList() != null) {
            getPlayList().clearQueue();
        }
        musicService.setState(PlaybackStateCompat.STATE_STOPPED);
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    public PlayQueue getPlayList() {
        return mPlayQueue;
    }


    public void setPlayList(PlayQueue playQueue) {
        this.mPlayQueue = playQueue;
    }

    public void stop() {
        musicService.setState(PlaybackStateCompat.STATE_STOPPED);
        currentProgress = getCurrentProgressInSong();
        musicService.stopService();
    }

    public Song getCurrentSong() {
        if (mPlayQueue != null) {
            return mPlayQueue.getCurrentPlay();
        } else {
            return null;
        }
    }

    public int getState() {
        if (musicService != null) {
            return musicService.getState();
        }
        return PlaybackStateCompat.STATE_STOPPED;
    }

    public void registerOnSongChangedListener(OnSongChangeListener listener) {
        onSongChangeListeners.add(listener);

    }

    public void unRegisterOnSongChangedListener(OnSongChangeListener listener) {
        onSongChangeListeners.remove(listener);

    }

    private int getCurrentProgressInSong() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : currentProgress;
    }

    public void seekTo(int progress) {
        if (mediaPlayer == null) {
            currentProgress = progress;
        } else {
            if (getCurrentProgressInSong() > progress) {
                musicService.setState(PlaybackStateCompat.STATE_REWINDING);
            } else {
                musicService.setState(PlaybackStateCompat.STATE_FAST_FORWARDING);
            }
            currentProgress = progress;
            mediaPlayer.seekTo(currentProgress);
        }
    }

    public final static int REPEAT_ALL = 0;
    public final static int REPEAT_SINGLE = 1;
    public final static int SHUFFLE_MODE = 2;

    private int currentPlayMode = REPEAT_ALL;

    public void switchPlayMode() {
        if (currentPlayMode == REPEAT_ALL) {
            SetPlayMode(REPEAT_ALL);
        } else if (currentPlayMode == REPEAT_SINGLE) {
            SetPlayMode(REPEAT_SINGLE);
        } else if (currentPlayMode == SHUFFLE_MODE) {
            SetPlayMode(SHUFFLE_MODE);
        }
    }

    private void SetPlayMode(int playMode) {
        if (playMode < 0 || playMode > 2) {
            throw new IllegalArgumentException("Incorrect Play Mode");
        }
        createMediaPlayerIfNeeded();
        currentPlayMode = playMode;

        if (playMode == REPEAT_SINGLE) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
    }

    public int getPlayMode() {
        return currentPlayMode;
    }

    public int getCurrentMaxDuration() {
        return currentMaxDuration;
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        currentMaxDuration = mediaPlayer.getDuration();
        configMediaPlayerState();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isLooping()) {
            // The media player finished playing the current song, so we go ahead and start the next.
            currentProgress = 0;
            playNextSong();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }
}
