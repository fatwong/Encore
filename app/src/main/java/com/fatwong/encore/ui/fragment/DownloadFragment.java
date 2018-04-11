package com.fatwong.encore.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.DownloadAdapter;
import com.fatwong.encore.base.BaseFragment;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends BaseFragment implements XExecutor.OnAllTaskEndListener {

    @BindView(R.id.start_all)
    RelativeLayout startAll;
    @BindView(R.id.pause_all)
    RelativeLayout pauseAll;
    @BindView(R.id.recycler_download)
    RecyclerView recyclerDownload;
    Unbinder unbinder;
    private OkDownload okDownload;
    private DownloadAdapter downloadAdapter;


    public DownloadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        unbinder = ButterKnife.bind(this, view);
        okDownload = OkDownload.getInstance();
        downloadAdapter = new DownloadAdapter(getActivity());
        downloadAdapter.updateData(DownloadAdapter.TYPE_DOWNLOADING);
        recyclerDownload.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerDownload.setAdapter(downloadAdapter);
        okDownload.addOnAllTaskEndListener(this);
        return view;
    }


    @Override
    public void onAllTaskEnd() {
        showSnackBar("所有下载任务已结束");
    }

    @Override
    public void onResume() {
        super.onResume();
        this.downloadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.okDownload.removeOnAllTaskEndListener(this);
        this.downloadAdapter.unRegister();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.start_all, R.id.pause_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_all:
                this.okDownload.startAll();
                break;
            case R.id.pause_all:
                this.okDownload.pauseAll();
                break;
        }
    }
}
