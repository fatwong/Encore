package com.fatwong.encore.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.MVRecyclerAdapter;
import com.fatwong.encore.bean.MV;
import com.fatwong.encore.bean.MVList;
import com.fatwong.encore.interfaces.APIService;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * A simple {@link Fragment} subclass.
 */
public class MVFragment extends Fragment {

    @BindView(R.id.mv_recycler)
    RecyclerView mvRecycler;
    Unbinder unbinder;

    private MVRecyclerAdapter mvRecyclerAdapter;

    public static MVFragment newInstance() {
        MVFragment fragment = new MVFragment();
        return fragment;
    }


    public MVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mv, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        mvRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mvRecyclerAdapter = new MVRecyclerAdapter(getActivity());
        mvRecycler.setAdapter(mvRecyclerAdapter);
        mvRecyclerAdapter.setOnMVClickListener(new MVRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String mvID, String mvName) {
                OkGo.<String>get(APIService.MV_INFO_URL + mvID)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Gson gson = new Gson();
                                MV mv = gson.fromJson(response.body(), MV.class);
                                JZVideoPlayerStandard.startFullscreen(getActivity(), JZVideoPlayerStandard.class,
                                        mv.getResult().getFiles().getValue31().getFile_link(),
                                        mv.getResult().getMv_info().getTitle());
                            }
                        });
            }
        });
        return view;
    }

    private void initData() {
        OkGo.<String>get(APIService.MV_URL)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Gson gson = new Gson();
                        MVList mvList = gson.fromJson(response.body(), MVList.class);
                        mvRecyclerAdapter.setData(mvList.getResult().getMv_list());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
