package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.R;
import com.fatwong.encore.adapter.RecentlyPlayedRecyclerAdapter;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.RecentPlaylist;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.lb.materialdesigndialog.base.DialogBase;
import com.lb.materialdesigndialog.base.DialogWithTitle;
import com.lb.materialdesigndialog.impl.MaterialDialogNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecentlyPlayedActivity extends AppCompatActivity implements OnSongChangeListener {

    @BindView(R.id.clear_button)
    Button clearButton;
    @BindView(R.id.recently_played_toolbar)
    TintToolbar recentlyPlayedToolbar;
    @BindView(R.id.recently_played_recycler)
    RecyclerView recentlyPlayedRecycler;

    private PlayQueue playQueue;
    private ActionBar actionBar;
    private RecentlyPlayedRecyclerAdapter recentlyPlayedRecyclerAdapter;

    public static void open(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, RecentlyPlayedActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_played);
        ButterKnife.bind(this);
        setToolBar();

        MusicPlayerManager.get().registerOnSongChangedListener(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        clearButton.setText("Clear");
        recentlyPlayedRecyclerAdapter = new RecentlyPlayedRecyclerAdapter(this);
        recentlyPlayedRecycler.setLayoutManager(new LinearLayoutManager(this));
        recentlyPlayedRecycler.setAdapter(recentlyPlayedRecyclerAdapter);
        recentlyPlayedRecyclerAdapter.setSongs(RecentPlaylist.getInstance().getRecentPlaylist());
        playQueue = new PlayQueue(RecentPlaylist.getInstance().getRecentPlaylist());

        recentlyPlayedRecyclerAdapter.setOnSongClickListener(new OnItemClickListener<Song>() {
            @Override
            public void onItemClick(Song song, int position) {
                MusicPlayerManager.get().playQueue(playQueue, position);
                startPlayingActivity();
            }

            @Override
            public void onItemSettingClick(View view, Song song, int position) {
                showPopupMenu(view, song, position);
            }
        });
    }

    private void showPopupMenu(final View view, final Song song, final int position) {
        final PopupMenu menu = new PopupMenu(this, view);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_song_play:
                        MusicPlayerManager.get().playQueue(playQueue, position);
                        startPlayingActivity();
                        break;
                    case R.id.popup_song_add_to_playlist:
                        PlayQueue mp = MusicPlayerManager.get().getPlayList();
                        if (mp == null) {
                            mp = new PlayQueue();
                            MusicPlayerManager.get().setPlayList(mp);
                        }
                        mp.addSong(song);
                        break;
                    case R.id.popup_song_fav:
                        break;
                    case R.id.popup_song_download:
                        break;
                }
                return false;
            }
        });
        menu.inflate(R.menu.popup_recently_playlist_setting);
        menu.show();
    }

    private void setToolBar() {
        setSupportActionBar(recentlyPlayedToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setTitle("Recently Played");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayerManager.get().unRegisterOnSongChangedListener(this);
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
    public void onSongChanged(Song song) {
        recentlyPlayedRecyclerAdapter.setSongs(RecentPlaylist.getInstance().getRecentPlaylist());
        playQueue = new PlayQueue(RecentPlaylist.getInstance().getRecentPlaylist());
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

    }

    @OnClick(R.id.clear_button)
    public void onViewClicked() {
        MaterialDialogNormal d = new MaterialDialogNormal(this);
        d.setTitle("");
        d.setMessage("清空全部所有最近播放记录?");
        d.setNegativeButton("清空", new DialogWithTitle.OnClickListener() {
            @Override
            public void click(DialogBase dialog, View view) {
                RecentPlaylist.getInstance().clearRecentPlaylist();
                recentlyPlayedRecyclerAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        d.setPositiveButton("取消", new DialogWithTitle.OnClickListener() {
            @Override
            public void click(DialogBase dialog, View view) {
                dialog.dismiss();
            }
        });
    }

    public Boolean startPlayingActivity() {
        if (MusicPlayerManager.get().getCurrentSong() == null) {
            Toast.makeText(RecentlyPlayedActivity.this, R.string.music_playing_none, Toast.LENGTH_SHORT).show();
            return false;
        }
        PlayingActivity.open(this);
        return true;
    }
}
