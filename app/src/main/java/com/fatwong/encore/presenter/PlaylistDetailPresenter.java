package com.fatwong.encore.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fatwong.encore.MyApplication;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.bean.PlaylistRelation;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.db.PlaylistManager;
import com.fatwong.encore.db.SongManager;
import com.fatwong.encore.interfaces.PlaylistDetailIView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlaylistDetailPresenter {

    private Playlist playlist;
    private PlaylistDetailIView playlistDetailView;

    public PlaylistDetailPresenter(Playlist playlist, PlaylistDetailIView playlistDetailView) {
        this.playlist = playlist;
        this.playlistDetailView = playlistDetailView;
    }

    public void init() {
        if (playlist == null) {
            playlistDetailView.fail(new Throwable("Playlist can't be null"));
        }
        final int playlistId = playlist.getId();
        Spanned title = Html.fromHtml(playlist.getTitle());
        Spanned desc = Html.fromHtml(playlist.getDescription());
        playlistDetailView.playlistDetail(playlistId, title, desc);

        Glide.with(MyApplication.getInstance())
                .asBitmap()
                .load(playlist.getCoverUrl())
                .apply(new RequestOptions().placeholder(R.drawable.ah1))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        playlistDetailView.playlistCover(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        playlistDetailView.playlistCover(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(),R.drawable.ah1));
                    }
                });
        
        refresh();
    }

    private void refresh() {
        final int id = playlist.getId();
        Observable.create(new ObservableOnSubscribe<List<PlaylistRelation>>() {
            @Override
            public void subscribe(ObservableEmitter<List<PlaylistRelation>> emitter) throws Exception {
                emitter.onNext(PlaylistManager.getInstance().getPlaylistRelationList(id));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<List<PlaylistRelation>, List<Song>>() {
                    @Override
                    public List<Song> apply(List<PlaylistRelation> playlistRelations) throws Exception {
                        List<Song> songs = new ArrayList<>();
                        for (PlaylistRelation relation: playlistRelations) {
                            Song song = SongManager.getInstance().querySong(relation.getSongId());
                            if (song != null) {
                                songs.add(song);
                            }
                        }
                        return songs;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Song>>() {
                    @Override
                    public void accept(List<Song> songs) throws Exception {
                        playlistDetailView.getSongs(songs);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        playlistDetailView.fail(throwable);
                    }
                });
    }
}
