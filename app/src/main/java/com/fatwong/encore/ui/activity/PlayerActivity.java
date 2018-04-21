package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.db.PlaylistManager;
import com.fatwong.encore.event.UpdatePlaylistEvent;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.fragment.PlayQueueFragment;
import com.fatwong.encore.ui.fragment.SongPopupFragment;
import com.fatwong.encore.utils.ImageUtils;
import com.fatwong.encore.utils.RxBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import me.wcy.lrcview.LrcView;

public class PlayerActivity extends AppCompatActivity implements OnSongChangeListener {

    @BindView(R.id.music_played_duration)
    TextView musicPlayedDuration;
    @BindView(R.id.playing_seek_bar)
    SeekBar playingSeekBar;
    @BindView(R.id.music_duration)
    TextView musicDuration;
    @BindView(R.id.playing_previous)
    ImageView playingPrevious;
    @BindView(R.id.playing_play)
    ImageView playingPlay;
    @BindView(R.id.playing_next)
    ImageView playingNext;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cover_image)
    ImageView coverImage;
    @BindView(R.id.playing_playlist)
    ImageView playingPlaylist;
    @BindView(R.id.lrcviewContainer)
    RelativeLayout lrcviewContainer;
    @BindView(R.id.lrc_small)
    LrcView lrcSmall;
    @BindView(R.id.headerView)
    FrameLayout headerView;
    @BindView(R.id.music_tool)
    LinearLayout musicTool;

    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        MusicPlayerManager.get().registerOnSongChangedListener(this);
        initData();
        initLrcView();
        getLrcData();
        updateProgress();
        updateData();
        makeStatusBarTransparent();
    }

    private void initLrcView() {
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coverImage.getVisibility() == View.VISIBLE) {
                    coverImage.setVisibility(View.INVISIBLE);
                    lrcviewContainer.setVisibility(View.VISIBLE);
                    musicTool.setVisibility(View.INVISIBLE);
                }
            }
        });
        lrcviewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lrcviewContainer.getVisibility() == View.VISIBLE) {
                    lrcviewContainer.setVisibility(View.INVISIBLE);
                    coverImage.setVisibility(View.VISIBLE);
                    musicTool.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onSongChanged(Song song) {
        this.song = song;
        updateData();
        getLrcData();
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

    }

    private void getLrcData() {
        lrcSmall.postDelayed(new Runnable() {
            @Override
            public void run() {
                lrcSmall.loadLrc(getLrcText(MusicPlayerManager.get().getCurrentSong().getId() + ".lrc"));
            }
        }, 3000);
    }

    private String getLrcText(String fileName) {
        String lrcText = null;
        try {
            File file = new File("sdcard/download/" + fileName);
            FileInputStream fis = new FileInputStream(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            lrcText = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcText;
    }

    private void initData() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        song = MusicPlayerManager.get().getCurrentSong();
        if (song == null) {
            finish();
        }

        playingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicPlayerManager.get().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateProgress() {
        Observable.interval(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        playingSeekBar.setMax(MusicPlayerManager.get().getCurrentMaxDuration());
                        playingSeekBar.setProgress(MusicPlayerManager.get().getCurrentPosition());
                        musicDuration.setText(formatDuration(MusicPlayerManager.get().getCurrentMaxDuration()));
                        musicPlayedDuration.setText(formatDuration(MusicPlayerManager.get().getCurrentPosition()));
                        lrcSmall.updateTime(MusicPlayerManager.get().getCurrentPosition());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void updateData() {
        final String coverUrl = song.getCoverUrl();
        ImageUtils.glideWith(this, coverUrl, R.drawable.ah1, coverImage);
        if (!TextUtils.isEmpty(song.getAlbumName())) {
            String albumName = song.getAlbumName();
            Spanned s = Html.fromHtml(albumName);
            getSupportActionBar().setTitle(s);
        }
        toolbar.setTitle(Html.fromHtml(song.getTitle()));
        if (MusicPlayerManager.get().getCurrentSong() != null) {
            playingPlay.setImageResource(R.drawable.play_rdi_btn_pause);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String formatDuration(int millSecDuration) {
        int durationSeconds = millSecDuration / 1000;
        int durationSecond = durationSeconds % 60;
        int durationMinute = (durationSeconds - durationSecond) / 60;
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(durationMinute) + ":" + decimalFormat.format(durationSecond);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicPlayerManager.get().unRegisterOnSongChangedListener(this);
    }

    public static void open(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PlayerActivity.class);
        context.startActivity(intent);
    }

    private void makeStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }

    @OnClick({R.id.playing_previous, R.id.playing_play, R.id.playing_next, R.id.playing_playlist, R.id.playing_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.playing_previous:
                MusicPlayerManager.get().playPreviousSong();
                break;
            case R.id.playing_play:
                if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    MusicPlayerManager.get().pause();
                    playingPlay.setImageResource(R.drawable.play_rdi_btn_play);
                } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PAUSED) {
                    MusicPlayerManager.get().play();
                    playingPlay.setImageResource(R.drawable.play_rdi_btn_pause);
                }
                break;
            case R.id.playing_next:
                MusicPlayerManager.get().playNextSong();
                break;
            case R.id.playing_playlist:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PlayQueueFragment playQueueFragment = new PlayQueueFragment();
                        playQueueFragment.show(getSupportFragmentManager(), "PlayQueueFragment");
                    }
                }, 60);
                break;
            case R.id.playing_more:
                SongPopupFragment songPopupFragment = SongPopupFragment.newInstance(song, this);
                songPopupFragment.show(getSupportFragmentManager(), "");
                break;
        }
    }

    @OnClick(R.id.playing_fav)
    public void onViewClicked() {
    }
}
