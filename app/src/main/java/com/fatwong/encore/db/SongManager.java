package com.fatwong.encore.db;

import android.text.TextUtils;

import com.fatwong.encore.bean.Song;
import com.fatwong.encore.db.dao.SongDao;
import com.fatwong.encore.utils.FileUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SongManager {

    private static SongManager instance;
    private Map<Long, Song> songLibrary;
    private SongDao songDao;

    public static SongManager getInstance() {
        if (instance == null) {
            instance = new SongManager();
        }
        return instance;
    }

    public SongManager() {
        songDao = new SongDao();
        songLibrary = new LinkedHashMap<>();
        updateSongLibrary();
    }

    /**
     * 异步更新歌曲信息
     */
    private void updateSongLibrary() {
        Observable.create(new ObservableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Song>> emitter) throws Exception {
                emitter.onNext(songDao.queryAll());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Function<List<Song>, ObservableSource<Song>>() {
                    @Override
                    public ObservableSource<Song> apply(List<Song> songs) throws Exception {
                        return Observable.fromIterable(songs);
                    }
                })
                .map(new Function<Song, Song>() {
                    @Override
                    public Song apply(Song song) throws Exception {
                        if (song.getDownload() == Song.DOWNLOAD_COMPLETE && !TextUtils.isEmpty(song.getPath())) {
                            if (!FileUtils.existFile(song.getPath())) {
                                song.setDownload(Song.DOWNLOAD_NONE);
                                insertOrUpdateSong(song);
                            }
                        }
                        return song;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Song>() {
                    @Override
                    public void accept(Song song) throws Exception {
                        songLibrary.put(song.getId(), song);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public Song querySong(long songId) {
        if (songLibrary.containsKey(songId)) {
            return songLibrary.get(songId);
        }
        return null;
    }

    public void updateSongFromLibrary(Song song) {
        if (songLibrary.containsKey(song.getId())) {
            Song cacheSong = songLibrary.get(song.getId());
            song.setDownload(cacheSong.getDownload());
            song.setPath(cacheSong.getPath());
        }
    }

    public void insertOrUpdateSong(final Song song) {
        Observable.create(new ObservableOnSubscribe<Song>() {
            @Override
            public void subscribe(ObservableEmitter<Song> emitter) throws Exception {
                updateSongFromLibrary(song);
                songDao.insertOrUpdateSong(song);
                emitter.onNext(song);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Song>() {
                    @Override
                    public void accept(Song song) throws Exception {
                        songLibrary.put(song.getId(), song);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 删除数据库的歌曲信息，包括下载中的temp缓存和已经下载完的歌曲
     *
     * @param song 歌曲信息
     */
    public void deleteSong(final Song song) {
       Observable.create(new ObservableOnSubscribe<Song>() {
           @Override
           public void subscribe(ObservableEmitter<Song> emitter) throws Exception {
               if (songLibrary.containsKey(song.getId())) {
                   songDao.deleteSong(song);
               }
               emitter.onNext(song);
           }
       }).subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<Song>() {
                   @Override
                   public void accept(Song song) throws Exception {
                       songLibrary.remove(song.getId());
                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(Throwable throwable) throws Exception {

                   }
               });
    }
}
