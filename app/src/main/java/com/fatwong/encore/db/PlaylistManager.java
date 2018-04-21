package com.fatwong.encore.db;

import android.util.Log;

import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.bean.PlaylistRelation;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.db.dao.PlaylistDao;
import com.fatwong.encore.db.dao.PlaylistRelationDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlaylistManager {

    private static PlaylistManager instance;
    private PlaylistDao playlistDao;
    private PlaylistRelationDao playlistRelationDao;
    private List<Playlist> allPlaylists;

    private PlaylistManager() {
        playlistDao = new PlaylistDao();
        playlistRelationDao = new PlaylistRelationDao();
        getAllPlaylistsFromDao();
    }

    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    /**
     * 提前异步从数据库获取歌单数据
     */
    private void getAllPlaylistsFromDao() {
        Observable.create(new ObservableOnSubscribe<List<Playlist>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Playlist>> emitter) throws Exception {
                emitter.onNext(playlistDao.queryAll());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Playlist>>() {
                    @Override
                    public void accept(List<Playlist> playlistsBean) throws Exception {
                        allPlaylists = playlistsBean;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public List<Playlist> getAllPlaylists() {
        if (allPlaylists == null) {
            allPlaylists = playlistDao.queryAll();
        }
        return allPlaylists;
    }

    public Playlist getPlaylistById(int id) {
        for (Playlist playlist: getAllPlaylists()) {
            if (playlist.getId() == id) {
                return playlist;
            }
        }
        return null;
    }

    public void deletePlaylist(Playlist playlist) {
        Observable.just(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Playlist, Integer>() {
                    @Override
                    public Integer apply(Playlist playlist) throws Exception {
                        int index = getPlaylistIndex(playlist);
                        playlistDao.deletePlaylist(playlist);
                        List<PlaylistRelation> playlistRelationList = getPlaylistRelationList(playlist.getId());
                        for (PlaylistRelation playlistRelation: playlistRelationList) {
                            Song song = SongManager.getInstance().querySong(playlistRelation.getSongId());
                            if (song != null && song.getDownload() == Song.DOWNLOAD_NONE) {
                                SongManager.getInstance().deleteSong(song);
                            }
                            deletePlaylistRelation(playlistRelation);
                        }
                        return index;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer index) throws Exception {
                        if (index > 0) {
                            getAllPlaylists().remove((int)index);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 获取歌单位置，若表中不存在则返回-1
     * @param playlist
     * @return
     */
    private int getPlaylistIndex(Playlist playlist) {
        for (Playlist playlistBean: getAllPlaylists()) {
            if (playlistBean.getId() == playlist.getId()) {
                int index = getAllPlaylists().indexOf(playlistBean);
                return index;
            }
        }
        return -1;
    }

    /**
     * 当数据库已经存在该收藏夹时，进行更新
     * 当数据库不存在该收藏夹时，进行插入
     *
     * @param playlist 歌单实例
     */

    public void setPlaylist(Playlist playlist) {
        int index = getPlaylistIndex(playlist);
        if (index < 0) {
            long id = playlistDao.insertPlaylist(playlist);
            playlist.setId((int) id);
            getAllPlaylists().add(playlist);
        } else {
            playlistDao.updatePlaylist(playlist);
            getAllPlaylists().set(index, playlist);
        }
    }

    public void setPlaylistAsync(Playlist playlist) {
        Observable.just(playlist)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Playlist>() {
                    @Override
                    public void accept(Playlist playlist) throws Exception {
                        setPlaylist(playlist);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /** 歌单关系表相关操作 **/

    /**
     * 获取某个歌单的所有关系
     * @param playlistId
     * @return
     */

    public List<PlaylistRelation> getPlaylistRelationList(int playlistId) {
        return playlistRelationDao.queryByPlaylistId(playlistId);
    }

    /**
     * 获取某个歌单与某首歌曲的关系
     * @param playlistId
     * @param songId
     * @return
     */

    public PlaylistRelation getPlaylistRelation(int playlistId, int songId) {
        return playlistRelationDao.queryByPlaylistIdAndSongId(playlistId, songId);
    }

    public long insertPlaylistRelation(Playlist playlist, Song song) {
        if (SongManager.getInstance().querySong(song.getId()) == null) {
            SongManager.getInstance().insertOrUpdateSong(song);
        }
        PlaylistRelation playlistRelation = new PlaylistRelation(-1, playlist.getId(), (int) song.getId());
        long index = playlistRelationDao.insertPlaylistRelation(playlistRelation);
        if (index > 0) {
            playlist.setCount(playlist.getCount() + 1);
            setPlaylist(playlist);
        }
        return index;
    }

    public void insertPlaylistRelationAsync(final Playlist playlist, final Song song, Consumer<Boolean> consumer) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                long index = PlaylistManager.getInstance().insertPlaylistRelation(playlist, song);
                emitter.onNext(index > 0);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 删除歌单的for循环中，删除每一首歌曲后，歌单count-1并删除相关联系
     * @param playlistRelation
     * @return
     */

    public int deletePlaylistRelation(PlaylistRelation playlistRelation) {
        Playlist playlist = getPlaylistById(playlistRelation.getPlaylistId());
        if (playlist != null && playlist.getCount() > 0) {
            playlist.setCount(playlist.getCount() - 1);
            setPlaylist(playlist);
        }
        return playlistRelationDao.deletePlaylistRelation(playlistRelation.getId());
    }

    public void deletePlaylistRelationAsync(final Playlist playlist, final Song song, Consumer<Boolean> consumer) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                long index = PlaylistManager.getInstance().deletePlaylistRelation(playlist.getId(), (int) song.getId());
                emitter.onNext(index > 0);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    public int deletePlaylistRelation(int playlistId, int songId) {
        Playlist playlist = getPlaylistById(playlistId);
        if (playlist != null && playlist.getCount() > 0) {
            playlist.setCount(playlist.getCount() -1);
            setPlaylist(playlist);
        }
        return playlistRelationDao.deletePlaylistRelation(playlistId, songId);
    }



}
