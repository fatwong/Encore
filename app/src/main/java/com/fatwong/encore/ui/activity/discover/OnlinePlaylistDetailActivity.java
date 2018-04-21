package com.fatwong.encore.ui.activity.discover;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.OnlinePlaylistDetailRecyclerAdapter;
import com.fatwong.encore.bean.OnlinePlaylistInfo;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.bean.SongInfo;
import com.fatwong.encore.interfaces.ICallback;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.interfaces.OnlinePlaylistDetailIView;
import com.fatwong.encore.presenter.MusicDetailPresenter;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.activity.PlayerActivity;
import com.fatwong.encore.ui.fragment.DownloadPopupFragment;
import com.fatwong.encore.utils.ImageUtils;
import com.fatwong.encore.utils.OkGoUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnlinePlaylistDetailActivity extends AppCompatActivity implements OnSongChangeListener, OnlinePlaylistDetailIView {

    private static String listID;
    @BindView(R.id.background_album_art)
    ImageView backgroundAlbumArt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.song_list)
    RecyclerView songRecycler;

    private MusicDetailPresenter musicDetailPresenter;
    private OnlinePlaylistDetailRecyclerAdapter detailRecyclerAdapter;
    private List<Song> songList = new ArrayList<>();

    public static void open(Context context, String listid) {
        listID = listid;
        Intent intent = new Intent();
        intent.setClass(context, OnlinePlaylistDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().setDuration(500);
        }
        MusicPlayerManager.get().registerOnSongChangedListener(this);
        setToolBar();
        detailRecyclerAdapter = new OnlinePlaylistDetailRecyclerAdapter(this);
        songRecycler.setLayoutManager(new LinearLayoutManager(this));
        musicDetailPresenter = new MusicDetailPresenter(this, this);
        musicDetailPresenter.initListData(0, listID);

    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setData(OnlinePlaylistInfo data) {
        ImageUtils.glideWith(this, data.getPic_300(), R.drawable.a8y, backgroundAlbumArt);
        toolbar.setTitle(data.getTitle());
        detailRecyclerAdapter.setData(data);
        songRecycler.setAdapter(detailRecyclerAdapter);
        detailRecyclerAdapter.setOnSongClickListener(new OnlinePlaylistDetailRecyclerAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(View view, final int position, final String songId) {
                final PlayQueue playQueue = new PlayQueue();
                OkGoUtils.getInstance().getSongInfo(songId, new ICallback() {
                    @Override
                    public void getSongInfoSuccess(SongInfo songInfo) {
                        SongInfo.SonginfoBean songinfoBean = songInfo.getSonginfo();
                        Song song = new Song(Long.parseLong(songinfoBean.getSong_id())
                                , songinfoBean.getTitle()
                                , Long.parseLong(songinfoBean.getAlbum_id())
                                , songinfoBean.getAlbum_title()
                                , Long.parseLong(songinfoBean.getArtist_id())
                                , songinfoBean.getAuthor(), songInfo.getBitrate().getFile_link()
                                , songInfo.getBitrate().getFile_size()
                                , songInfo.getBitrate().getFile_duration()
                                , 1213142, "100", position, songinfoBean.getAlbum_title()
                                , songinfoBean.getPic_premium()
                                , 1
                                , songInfo.getBitrate().getFile_link(), false);
                        songList.add(song);
                        playQueue.addQueue(songList, true);
                        MusicPlayerManager.get().playQueue(playQueue, position);
                        OkGo.<File>get(songinfoBean.getLrclink())
                                .tag(OnlinePlaylistDetailActivity.this)
                                .execute(new FileCallback(songinfoBean.getSong_id()+".lrc") {
                                    @Override
                                    public void onSuccess(Response<File> response) {
                                    }
                                });
                        PlayerActivity.open(OnlinePlaylistDetailActivity.this);

                    }

                    @Override
                    public void getSongInfoFailed(Throwable e) {

                    }
                });
            }

            @Override
            public void onSongSettingsClick(View view, int position, OnlinePlaylistInfo onlinePlaylistInfo) {
                DownloadPopupFragment.newInstance(onlinePlaylistInfo, OnlinePlaylistDetailActivity.this, position).show(getSupportFragmentManager(), "OnlinePlaylistPopup");
            }
        });
    }

    @Override
    public void onSongChanged(Song song) {

    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

    }

    @Override
    public void loadMusicDetailData(OnlinePlaylistInfo baseBean) {
        setData(baseBean);
    }

    @Override
    public void loadFail(Throwable e) {

    }
}
