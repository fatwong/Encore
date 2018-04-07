package com.fatwong.encore.bean;

import com.fatwong.encore.MyApplication;
import com.fatwong.encore.utils.CacheUtils;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by isaac on 27/3/2018.
 */

public class RecentPlaylist {

    private static RecentPlaylist instance;

    private ArrayList<Song> queue;
    private int RECENT_PLAYED_MAX = 20;
    private String RECENT_CACHE_TAG = "Recent Play Queue";

    public static RecentPlaylist getInstance() {
        if (instance == null) {
            instance = new RecentPlaylist();
        }
        return instance;
    }

    public ArrayList<Song> getRecentPlaylist() {
        return queue;
    }

    public void clearRecentPlaylist() {
        queue.clear();
    }

    private RecentPlaylist() {
        queue = readQueueFromFileCache();
    }

    public void addPlayedSong(Song song) {
        queue.add(0, song);
        for (int i = queue.size() - 1; i > 0; i--) {
            if (i > RECENT_PLAYED_MAX) {
                queue.remove(i);
                continue;
            }

            if (song.getId() == queue.get(i).getId()) {
                queue.remove(i);
                break;
            }
        }

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                addQueueToFileCache();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * 将最近播放歌曲列表存入缓存
     */
    private void addQueueToFileCache() {
        CacheUtils.get(MyApplication.getInstance(), RECENT_CACHE_TAG).put(RECENT_CACHE_TAG, queue);
    }

    private ArrayList<Song> readQueueFromFileCache() {
        Object object = CacheUtils.get(MyApplication.getInstance(), RECENT_CACHE_TAG).getAsObject(RECENT_CACHE_TAG);
        if (object != null && object instanceof ArrayList) {
            return (ArrayList<Song>) object;
        }

        return new ArrayList<>();
    }
}
