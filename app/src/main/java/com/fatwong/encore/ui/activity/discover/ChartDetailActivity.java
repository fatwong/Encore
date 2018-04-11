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
import com.fatwong.encore.adapter.ChartDetailRecyclerAdapter;
import com.fatwong.encore.adapter.OnlinePlaylistDetailRecyclerAdapter;
import com.fatwong.encore.bean.ChartInfo;
import com.fatwong.encore.bean.OnlinePlaylistInfo;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.bean.SongInfo;
import com.fatwong.encore.interfaces.ChartDetailIView;
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

public class ChartDetailActivity extends AppCompatActivity implements OnSongChangeListener, ChartDetailIView {

    private static String chartId;
    @BindView(R.id.background_album_art)
    ImageView backgroundAlbumArt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.song_list)
    RecyclerView songRecycler;

    private MusicDetailPresenter musicDetailPresenter;
    private ChartDetailRecyclerAdapter detailRecyclerAdapter;
    private List<Song> songList = new ArrayList<>();

    public static void open(Context context, String id) {
        chartId = id;
        Intent intent = new Intent();
        intent.setClass(context, ChartDetailActivity.class);
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
        detailRecyclerAdapter = new ChartDetailRecyclerAdapter(this);
        songRecycler.setLayoutManager(new LinearLayoutManager(this));
        musicDetailPresenter = new MusicDetailPresenter(this, this);
        musicDetailPresenter.initListData(1, chartId);
    }

    private void setData(ChartInfo data) {
        ImageUtils.glideWith(this, data.getBillboard().getPic_s192(), R.drawable.a8p, backgroundAlbumArt);
        toolbar.setTitle(data.getBillboard().getName());
        detailRecyclerAdapter.setData(data);
        songRecycler.setAdapter(detailRecyclerAdapter);
        detailRecyclerAdapter.setOnItemClickLitener(new ChartDetailRecyclerAdapter.OnSongClickListener() {
            @Override
            public void onSongClick(View view, final int position, String songId) {
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
                                , 0
                                , songInfo.getBitrate().getFile_link(), false);
                        songList.add(song);
                        playQueue.addQueue(songList, true);
                        MusicPlayerManager.get().playQueue(playQueue, position);
                        OkGo.<File>get(songinfoBean.getLrclink())
                                .tag(ChartDetailActivity.this)
                                .execute(new FileCallback(songinfoBean.getSong_id()+".lrc") {
                                    @Override
                                    public void onSuccess(Response<File> response) {
                                    }
                                });
                        PlayerActivity.open(ChartDetailActivity.this);
                    }

                    @Override
                    public void getSongInfoFailed(Throwable e) {

                    }
                });
            }

            @Override
            public void onSongSettingsClick(View view, int position, ChartInfo chartInfo) {
                DownloadPopupFragment.newInstance(chartInfo, ChartDetailActivity.this, position).show(getSupportFragmentManager(), "ChartDetailPopup");
            }
        });
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

    @Override
    public void onSongChanged(Song song) {

    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

    }

    @Override
    public void loadMusicDetailData(ChartInfo chartInfoBean) {
        setData(chartInfoBean);
    }

    @Override
    public void loadFail(Throwable e) {

    }
}
