package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.R;
import com.fatwong.encore.adapter.LocalArtistDetailAdapter;
import com.fatwong.encore.base.BaseActivity;
import com.fatwong.encore.bean.Artist;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.fragment.SongPopupFragment;
import com.fatwong.encore.utils.LocalMusicLibrary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalArtistDetailActivity extends BaseActivity {

    @BindView(R.id.artist_detail_toolbar)
    TintToolbar artistDetailToolbar;
    @BindView(R.id.artist_detail_recycler)
    RecyclerView artistDetailRecycler;

    private ActionBar actionBar;
    private LocalArtistDetailAdapter localArtistDetailAdapter;
    private Artist currentArtist;
    private PlayQueue playQueue;
    private int currentSongPosition = -1;

    public static void open(Context context, Artist artist) {
        context.startActivity(getIntent(context, artist));
    }

    private static Intent getIntent(Context context, Artist artist) {
        Intent intent = new Intent();
        intent.setClass(context, LocalArtistDetailActivity.class);
        intent.putExtra("artist", artist);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_artist_detail);
        ButterKnife.bind(this);
        playQueue = new PlayQueue();
        currentArtist = getIntent().getParcelableExtra("artist");
        if (currentArtist == null) {
            finish();
        }
        setToolBar();
        localArtistDetailAdapter = new LocalArtistDetailAdapter(this);
        artistDetailRecycler.setLayoutManager(new LinearLayoutManager(this));
        artistDetailRecycler.setAdapter(localArtistDetailAdapter);
        artistDetailRecycler.setHasFixedSize(true);
        reloadAdapter();
        localArtistDetailAdapter.setOnSongClickListener(new OnItemClickListener<Song>() {
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
                SongPopupFragment songPopupFragment = SongPopupFragment.newInstance(item, LocalArtistDetailActivity.this);
                songPopupFragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Song> songs = LocalMusicLibrary.getArtistAllSongs(LocalArtistDetailActivity.this, (int) currentArtist.artist_id);
                if (songs != null) {
                    playQueue.setQueue(songs);
                    localArtistDetailAdapter.setSongs(songs);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                localArtistDetailAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void setToolBar() {
        setSupportActionBar(artistDetailToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setTitle(currentArtist.artist_name);
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
