package com.fatwong.encore.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fatwong.encore.R;
import com.fatwong.encore.base.BaseFragment;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.activity.PlayerActivity;
import com.fatwong.encore.utils.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomFragment extends BaseFragment implements OnSongChangeListener {

    @BindView(R.id.play_bar_cover)
    SimpleDraweeView playBarCover;
    @BindView(R.id.play_bar_title)
    TextView playBarTitle;
    @BindView(R.id.play_bar_singer)
    TextView playBarSinger;
    @BindView(R.id.play_bar_playlist)
    TintImageView playBarPlaylist;
    @BindView(R.id.play_bar_play)
    TintImageView playBarPlay;
    @BindView(R.id.play_bar_next)
    TintImageView playBarNext;
    @BindView(R.id.play_bar_blank)
    LinearLayout playBarLinear;
    Unbinder unbinder;
    @BindView(R.id.play_bar_layout)
    LinearLayout playBarLayout;

    private Song song;
    private boolean currentSongIsPaused;

    public static BottomFragment newInstance() {
        return new BottomFragment();
    }


    public BottomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bottom, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        MusicPlayerManager.get().registerOnSongChangedListener(this);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song = MusicPlayerManager.get().getCurrentSong();
                if (song == null) {
                    if (song == null) {
                        Toast.makeText(getActivity(), "当前无歌曲播放", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }
        });
        return rootView;
    }

    private void updateData() {
        String coverUrl = song.getCoverUrl();
        if (!currentSongIsPaused) {
            ImageUtils.GlideWith(getActivity(), coverUrl, R.drawable.ah1, playBarCover);
        }
        if (!TextUtils.isEmpty(song.getTitle())) {
            String title = song.getTitle();
            playBarTitle.setText(title);

        }
        if (!TextUtils.isEmpty(song.getArtistName())) {
            String artistName = song.getArtistName();
            playBarSinger.setText(artistName);

        }
        if (MusicPlayerManager.get().getCurrentSong() != null) {
            playBarPlay.setImageResource(R.drawable.playbar_btn_pause);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        this.song = song;
        updateData();
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {
        if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
            playBarPlay.setImageResource(R.drawable.playbar_btn_pause);
        } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
            playBarPlay.setImageResource(R.drawable.playbar_btn_play);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        currentSongIsPaused = false;
    }

    @Override
    public void onPause() {
        currentSongIsPaused = true;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicPlayerManager.get().unRegisterOnSongChangedListener(this);
    }

    @OnClick({R.id.play_bar_playlist, R.id.play_bar_play, R.id.play_bar_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play_bar_playlist:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PlayQueueFragment playQueueFragment = new PlayQueueFragment();
                        playQueueFragment.show(getFragmentManager(), "PlayQueueFragment");
                    }
                }, 60);
                break;
            case R.id.play_bar_play:
                if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    MusicPlayerManager.get().pause();
                    playBarPlay.setImageResource(R.drawable.playbar_btn_play);
                } else if (MusicPlayerManager.get().getState() == PlaybackStateCompat.STATE_PAUSED) {
                    MusicPlayerManager.get().play();
                    playBarPlay.setImageResource(R.drawable.playbar_btn_pause);
                }
                break;
            case R.id.play_bar_next:
                MusicPlayerManager.get().playNextSong();
                break;
        }
    }
}
