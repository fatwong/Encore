package com.fatwong.encore.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.SearchSongRecyclerAdapter;
import com.fatwong.encore.bean.PlayQueue;
import com.fatwong.encore.bean.SearchSongInfo;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.bean.SongInfo;
import com.fatwong.encore.interfaces.ICallback;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.activity.PlayerActivity;
import com.fatwong.encore.utils.OkGoUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetSearchSongFragment extends Fragment {


    @BindView(R.id.search_song_recycler)
    RecyclerView searchSongRecycler;
    Unbinder unbinder;
    private SearchSongRecyclerAdapter adapter;
    private ArrayList<SearchSongInfo> searchSongInfos;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private List<Song> songList = new ArrayList<>();

    public static NetSearchSongFragment newInstance(ArrayList<SearchSongInfo> list) {
        NetSearchSongFragment fragment = new NetSearchSongFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("searchMusic", list);
        fragment.setArguments(bundle);
        return fragment;
    }

    public NetSearchSongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_search_song, container, false);
        if (getArguments() != null) {
            searchSongInfos = getArguments().getParcelableArrayList("searchMusic");
        }
        unbinder = ButterKnife.bind(this, view);

        layoutManager = new LinearLayoutManager(mContext);
        searchSongRecycler.setLayoutManager(layoutManager);
        adapter = new SearchSongRecyclerAdapter(mContext, searchSongInfos);
        searchSongRecycler.setAdapter(adapter);
        searchSongRecycler.setHasFixedSize(true);
        searchSongRecycler.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        adapter.setOnSongClickListener(new SearchSongRecyclerAdapter.OnSongClickListener() {
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
                                .tag(NetSearchSongFragment.this)
                                .execute(new FileCallback(songinfoBean.getSong_id()+".lrc") {
                                    @Override
                                    public void onSuccess(Response<File> response) {
                                    }
                                });
                        PlayerActivity.open(mContext);

                    }

                    @Override
                    public void getSongInfoFailed(Throwable e) {

                    }
                });
            }

            @Override
            public void onSongSettingsClick(View view, int position, ArrayList<SearchSongInfo> songInfos) {
                DownloadPopupFragment.newInstance(songInfos, mContext, position).show(getFragmentManager(), "NetSearchSongFragment");
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}