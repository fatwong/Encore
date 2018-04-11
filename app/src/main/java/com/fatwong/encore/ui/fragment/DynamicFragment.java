package com.fatwong.encore.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.VideoRecyclerAdapter;
import com.fatwong.encore.bean.Dynamic;
import com.fatwong.encore.interfaces.APIService;
import com.fatwong.encore.ui.EndlessOnScrollListener;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jzvd.JZVideoPlayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DynamicFragment extends Fragment {


    @BindView(R.id.dynamic_recycler)
    RecyclerView dynamicRecycler;
    Unbinder unbinder;

    private LinearLayoutManager layoutManager;
    private VideoRecyclerAdapter videoRecyclerAdapter;
    private List<Dynamic.BodyBean.DetailBean> resultData = new ArrayList<>();
    private int num = 1;

    public DynamicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData(num);
        layoutManager = new LinearLayoutManager(getActivity());
        videoRecyclerAdapter = new VideoRecyclerAdapter(getActivity());
        dynamicRecycler.setLayoutManager(layoutManager);
        dynamicRecycler.setAdapter(videoRecyclerAdapter);
        dynamicRecycler.addOnScrollListener(new EndlessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                initData(currentPage ++);
            }
        });
        return view;
    }

    private void initData(int num) {
        OkGo.<String>get(APIService.DYNAMIC_URL + num)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("Testing", "HIHI");
                        String body = response.body();
                        String decodeStr = null;
                        try {
                            decodeStr = URLDecoder.decode(body, "utf-8");
                            Gson gson = new Gson();
                            Dynamic dynamic = gson.fromJson(decodeStr, Dynamic.class);
                            List<Dynamic.BodyBean.DetailBean> result = dynamic.getBody().getDetail();
                            resultData.addAll(result);
                            videoRecyclerAdapter.setData(resultData);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            JZVideoPlayer.releaseAllVideos();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
