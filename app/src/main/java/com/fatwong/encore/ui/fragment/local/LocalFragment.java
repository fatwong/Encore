package com.fatwong.encore.ui.fragment.local;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.PlaylistRecyclerAdapter;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.event.UpdatePlaylistEvent;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.ui.activity.CreatePlaylistActivity;
import com.fatwong.encore.ui.activity.DownloadActivity;
import com.fatwong.encore.ui.activity.LocalMusicActivity;
import com.fatwong.encore.ui.activity.PlaylistDetailActivity;
import com.fatwong.encore.ui.activity.RecentlyPlayedActivity;
import com.fatwong.encore.ui.fragment.PlaylistPopupFragment;
import com.fatwong.encore.utils.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.playlist_recycler)
    RecyclerView playlistRecycler;

    private PlaylistRecyclerAdapter playlistRecyclerAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LocalFragment() {
        // Required empty public constructor
    }

    public static LocalFragment newInstance() {
        LocalFragment localFragment = new LocalFragment();
        return localFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeDisposable.add(RxBus.getInstance()
                .register(UpdatePlaylistEvent.class)
                .subscribe(new Consumer<UpdatePlaylistEvent>() {
                    @Override
                    public void accept(UpdatePlaylistEvent event) throws Exception {
                        onEvent(event);
                    }
                }));
    }

    private void onEvent(UpdatePlaylistEvent event) {
        playlistRecyclerAdapter.updatePlaylist();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        unbinder = ButterKnife.bind(this, view);
        playlistRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        playlistRecyclerAdapter = new PlaylistRecyclerAdapter(getActivity());
        playlistRecycler.setAdapter(playlistRecyclerAdapter);
        playlistRecyclerAdapter.setOnPlaylistClickListener(new OnItemClickListener<Playlist>() {
            @Override
            public void onItemClick(Playlist item, int position) {
                PlaylistDetailActivity.open(getActivity(), item);
            }

            @Override
            public void onItemSettingClick(View view, Playlist item, int position) {
                PlaylistPopupFragment playlistPopupFragment = PlaylistPopupFragment.newInstance(position, item, playlistRecyclerAdapter);
                playlistPopupFragment.show(getFragmentManager(), "");
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @OnClick({R.id.local_music_layout, R.id.recently_played_layout, R.id.downloads_layout, R.id.artist_layout, R.id.add_playlist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.local_music_layout:
                startActivity(new Intent(getActivity(), LocalMusicActivity.class));
                break;
            case R.id.recently_played_layout:
                RecentlyPlayedActivity.open(getActivity());
                break;
            case R.id.downloads_layout:
                DownloadActivity.open(getActivity());
                break;
            case R.id.artist_layout:
                break;
            case R.id.add_playlist:
                CreatePlaylistActivity.open(getActivity());
                break;
        }
    }
}
