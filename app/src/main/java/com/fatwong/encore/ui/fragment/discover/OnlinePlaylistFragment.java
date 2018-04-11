package com.fatwong.encore.ui.fragment.discover;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.OnlinePlaylistRecyclerAdapter;
import com.fatwong.encore.base.BaseFragment;
import com.fatwong.encore.bean.OnlinePlaylist;
import com.fatwong.encore.interfaces.APIService;
import com.fatwong.encore.ui.activity.discover.OnlinePlaylistDetailActivity;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlinePlaylistFragment extends BaseFragment {


    @BindView(R.id.playlist_recycler)
    RecyclerView playlistRecycler;
    Unbinder unbinder;

    private OnlinePlaylistRecyclerAdapter onlinePlaylistRecyclerAdapter;

    public OnlinePlaylistFragment() {
        // Required empty public constructor
    }

    public static OnlinePlaylistFragment newInstance() {
        OnlinePlaylistFragment fragment = new OnlinePlaylistFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_playlist, container, false);
        unbinder = ButterKnife.bind(this, view);
        onlinePlaylistRecyclerAdapter = new OnlinePlaylistRecyclerAdapter(getActivity());
        playlistRecycler.setAdapter(onlinePlaylistRecyclerAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        playlistRecycler.setLayoutManager(gridLayoutManager);
        onlinePlaylistRecyclerAdapter.setOnPlaylistClickListener(new OnlinePlaylistRecyclerAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(View view, int position, String listId) {
                OnlinePlaylistDetailActivity.open(getActivity(),listId);
            }
        });
        getData();
        return view;
    }

    private void getData() {
        OkGo.<String>get(APIService.PLAYLIST_URL)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        OnlinePlaylist onlinePlaylist = gson.fromJson(response.body(), OnlinePlaylist.class);
                        onlinePlaylistRecyclerAdapter.setData(onlinePlaylist.getContent());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
