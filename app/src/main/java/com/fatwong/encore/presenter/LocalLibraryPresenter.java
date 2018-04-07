package com.fatwong.encore.presenter;


import android.content.Context;

import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.LocalIView;
import com.fatwong.encore.utils.LocalMusicLibrary;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Isaac on 2018/3/6.
 */

public class LocalLibraryPresenter {

    private Context mContext;
    private LocalIView.LocalMusic mLocalMusic;

    public LocalLibraryPresenter(Context context, LocalIView.LocalMusic localMusic) {
        this.mContext = context;
        this.mLocalMusic = localMusic;
    }

    public void requestMusic() {
        Observable.create(new ObservableOnSubscribe<List<Song>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Song>> emitter) throws Exception {
                List<Song> songs = LocalMusicLibrary.getAllSongs(mContext);
                emitter.onNext(songs);
            }
        }).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Song>>() {
            @Override
            public void accept(List<Song> songs) throws Exception {
                //歌曲获取成功
                if (mLocalMusic != null) {
                    mLocalMusic.getLocalMusicSuccess(songs);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                //歌曲获取失败
                if (mLocalMusic != null) {
                    mLocalMusic.getLocalMusicFailed(throwable);
                }
            }
        });
    }
}
