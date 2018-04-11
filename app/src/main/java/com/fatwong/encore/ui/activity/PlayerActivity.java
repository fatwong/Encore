package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.fragment.PlayQueueFragment;
import com.fatwong.encore.ui.fragment.SongPopupFragment;
import com.fatwong.encore.utils.ImageUtils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

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

    private Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        MusicPlayerManager.get().registerOnSongChangedListener(this);
        initData();
        updateProgress();
        updateData();
    }


    @Override
    public void onSongChanged(Song song) {
        this.song = song;
        updateData();
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

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
        toolbar.setTitle(song.getTitle());
        if (MusicPlayerManager.get().getCurrentSong() != null) {
            playingPlay.setImageResource(R.drawable.play_rdi_btn_pause);
        }
    }

    public String formatDuration(int millSecDuration) {
        int durationSeconds = millSecDuration / 1000;
        int durationSecond = durationSeconds % 60;
        int durationMinute = (durationSeconds - durationSecond) / 60;
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(durationMinute) + ":" + decimalFormat.format(durationSecond);
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
        }
    }

}
