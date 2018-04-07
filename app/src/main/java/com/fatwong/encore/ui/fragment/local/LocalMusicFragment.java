package com.fatwong.encore.ui.fragment.local;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.LocalMusicRecyclerAdapter;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.interfaces.LocalIView;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.presenter.LocalLibraryPresenter;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.base.BaseFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalMusicFragment extends BaseFragment implements LocalIView.LocalMusic {

    @BindView(R.id.local_recycler)
    RecyclerView localRecycler;
    Unbinder unbinder;
    private LocalMusicRecyclerAdapter localMusicRecyclerAdapter;
    private LocalLibraryPresenter localLibraryPresenter;
    private PlayQueue playQueue;


    public static LocalMusicFragment newInstance() {
        LocalMusicFragment localMusicFragment = new LocalMusicFragment();
        return localMusicFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localLibraryPresenter = new LocalLibraryPresenter(getActivity(), this);
        playQueue = new PlayQueue();
    }

    public LocalMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_local_music, container, false);
        unbinder = ButterKnife.bind(this, inflate);
        localMusicRecyclerAdapter = new LocalMusicRecyclerAdapter(getActivity());
        localRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        localRecycler.setAdapter(localMusicRecyclerAdapter);
        localMusicRecyclerAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Object item, int position) {
                MusicPlayerManager.get().playQueue(playQueue, position);
                startPlayingActivity();
            }

            @Override
            public void onItemSettingClick(View view, Object item, int position) {

            }
        });
        localLibraryPresenter.requestMusic();


        return inflate;
    }

    @Override
    public void getLocalMusicSuccess(List<Song> songs) {
        playQueue.setQueue(songs);
        localMusicRecyclerAdapter.setSongs(songs);
    }

    @Override
    public void getLocalMusicFailed(Throwable throwable) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
