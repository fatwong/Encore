package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.R;
import com.fatwong.encore.adapter.LocalAlbumDetailRecyclerAdapter;
import com.fatwong.encore.base.BaseActivity;
import com.fatwong.encore.bean.Album;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.fragment.SongPopupFragment;
import com.fatwong.encore.utils.LocalMusicLibrary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalAlbumDetailActivity extends BaseActivity {

    @BindView(R.id.album_detail_toolbar)
    TintToolbar albumDetailToolbar;
    @BindView(R.id.album_detail_recycler)
    RecyclerView albumDetailRecycler;

    private ActionBar actionBar;
    private LocalAlbumDetailRecyclerAdapter localAlbumDetailRecyclerAdapter;
    private Album currentAlbum;
    private PlayQueue playQueue;
    private int currentSongPosition = -1;


    public static void open(Context context, Album album) {
        context.startActivity(getIntent(context, album));
    }

    private static Intent getIntent(Context context, Album album) {
        Intent intent = new Intent();
        intent.setClass(context, LocalAlbumDetailActivity.class);
        intent.putExtra("album", album);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_album_detail);
        ButterKnife.bind(this);
        playQueue = new PlayQueue();
        currentAlbum = getIntent().getParcelableExtra("album");
        if (currentAlbum == null) {
            finish();
        }
        setToolBar();
        localAlbumDetailRecyclerAdapter = new LocalAlbumDetailRecyclerAdapter(this);
        albumDetailRecycler.setLayoutManager(new LinearLayoutManager(this));
        albumDetailRecycler.setAdapter(localAlbumDetailRecyclerAdapter);
        albumDetailRecycler.setHasFixedSize(true);
        reloadAdapter();
        localAlbumDetailRecyclerAdapter.setOnSongClickListener(new OnItemClickListener<Song>() {
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
                SongPopupFragment songPopupFragment = SongPopupFragment.newInstance(item, LocalAlbumDetailActivity.this);
                songPopupFragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Song> songs = LocalMusicLibrary.getAlbumAllSongs(LocalAlbumDetailActivity.this, currentAlbum.album_id);
                if (songs != null) {
                    playQueue.setQueue(songs);
                    localAlbumDetailRecyclerAdapter.setSongs(songs);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                localAlbumDetailRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void setToolBar() {
        setSupportActionBar(albumDetailToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setTitle(currentAlbum.album_name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
