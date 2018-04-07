package com.fatwong.encore.bean;

import com.fatwong.encore.service.MusicPlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Isaac on 2018/3/18.
 */

public class PlayQueue {

    private List<Song> queue;
    private Song currentSong;
    private long albumID;
    private String name;


    public long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayQueue(List<Song> queue) {
        setQueue(queue);
    }

    public PlayQueue() {
        queue = new ArrayList<>();
    }

    public List<Song> getQueue() {
        return queue;
    }

    public void setQueue(List<Song> queue) {
        this.queue = queue;
        setCurrentPlay(0);
    }

    public long setCurrentPlay(int position) {
        if (queue.size() > position && position >= 0) {
            currentSong = queue.get(position);
            return currentSong.getId();
        }
        return -1;
    }

    public Song getCurrentPlay() {
        if (currentSong == null) {
            setCurrentPlay(0);
        }
        return currentSong;
    }

    public void addSong(Song song) {
        queue.add(song);
    }

    public void addSong(Song song, int position) {
        queue.add(position, song);
    }

    public Song getPreviousSong() {
        int curIndex = queue.indexOf(currentSong);
        //根据播放模式确定下一首歌曲
        int playMode = MusicPlayerManager.get().getPlayMode();
        if (playMode == MusicPlayerManager.REPEAT_SINGLE || playMode == MusicPlayerManager.REPEAT_ALL) {
            if (--curIndex < 0) {
                curIndex = 0;
            }
        } else {
            curIndex = new Random().nextInt(queue.size());
        }
        currentSong = queue.get(curIndex);
        return currentSong;
    }

    public Song getNextSong() {
        int curIndex = queue.indexOf(currentSong);
        //根据播放模式确定下一首歌曲
        int playMode = MusicPlayerManager.get().getPlayMode();
        if (playMode == MusicPlayerManager.REPEAT_SINGLE || playMode == MusicPlayerManager.REPEAT_ALL) {
            if (++curIndex < 0) {
                curIndex = 0;
            }
        } else {
            curIndex = new Random().nextInt(queue.size());
        }
        currentSong = queue.get(curIndex);
        return currentSong;
    }

    public void clearQueue() {
        queue.clear();
        currentSong = null;
    }

}
