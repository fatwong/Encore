package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.PlaylistDetailRecyclerAdapter;
import com.fatwong.encore.base.BaseActivity;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.event.UpdatePlaylistEvent;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.interfaces.PlaylistDetailIView;
import com.fatwong.encore.presenter.PlaylistDetailPresenter;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.fragment.SongPopupFragment;
import com.fatwong.encore.utils.ImageUtils;
import com.fatwong.encore.utils.RxBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class PlaylistDetailActivity extends BaseActivity implements PlaylistDetailIView {

    @BindView(R.id.background_album_art)
    ImageView backgroundAlbumArt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.song_list)
    RecyclerView songList;
    @BindView(R.id.avatar_layout)
    LinearLayout avatarLayout;
    @BindView(R.id.playlist_collect_view)
    ImageView playlistCollectView;

    private Playlist currentPlaylist;
    private PlayQueue playQueue;
    private int currentSongPosition = -1;
    private PlaylistDetailPresenter playlistDetailPresenter;
    private PlaylistDetailRecyclerAdapter playlistDetailAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static void open(Context context, Playlist playlist) {
        context.startActivity(getIntent(context, playlist));
    }

    private static Intent getIntent(Context context, Playlist playlist) {
        Intent intent = new Intent();
        intent.setClass(context, PlaylistDetailActivity.class);
        intent.putExtra("playlist", playlist);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);
        setToolBar();
        playQueue = new PlayQueue();
        playlistDetailAdapter = new PlaylistDetailRecyclerAdapter(this);
        songList.setAdapter(playlistDetailAdapter);
        songList.setLayoutManager(new LinearLayoutManager(this));
        currentPlaylist = (Playlist) getIntent().getSerializableExtra("playlist");
        if (currentPlaylist == null) {
            finish();
        }
        ImageUtils.glideWith(this, currentPlaylist.getCoverUrl(), R.drawable.a8y, backgroundAlbumArt);
        toolbar.setTitle(currentPlaylist.getTitle());
        playlistDetailPresenter = new PlaylistDetailPresenter(currentPlaylist, this);
        playlistDetailPresenter.init();
        playlistDetailAdapter.setOnSongClickListener(new OnItemClickListener<Song>() {
            @Override
            public void onItemClick(Song item, int position) {
                if (currentSongPosition != position) {
                    MusicPlayerManager.get().playQueue(playQueue, position);
                    currentSongPosition = position;
                }
                startPlayingActivity();
            }

            @Override
            public void onItemSettingClick(View view, Song item, int position) {
                SongPopupFragment songPopupFragment = SongPopupFragment.newInstance(item, PlaylistDetailActivity.this, currentPlaylist, playlistDetailAdapter);
                songPopupFragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.actionbar_back);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void playlistDetail(int collectionId, Spanned title, Spanned description) {
    }

    @Override
    public void playlistCover(Bitmap cover) {

    }

    @Override
    public void getSongs(List<Song> songs) {
        playQueue.setQueue(songs);
        playlistDetailAdapter.setSongs(songs);
        songList.addItemDecoration(new DividerItemDecoration(PlaylistDetailActivity.this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void fail(Throwable throwable) {

    }
}
