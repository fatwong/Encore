package com.fatwong.encore.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.fatwong.encore.R;
import com.fatwong.encore.adapter.DownloadAdapter;
import com.fatwong.encore.base.BaseFragment;
import com.fatwong.encore.event.DownloadCompleteEvent;
import com.fatwong.encore.utils.RxBus;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadCompleteFragment extends BaseFragment implements XExecutor.OnAllTaskEndListener {

    @BindView(R.id.play_all_song)
    TintImageView playAllSong;
    @BindView(R.id.play_all_song_text)
    TextView playAllSongText;
    @BindView(R.id.play_all_song_count)
    TextView playAllSongCount;
    @BindView(R.id.multi_select)
    ImageView multiSelect;
    @BindView(R.id.play_all_song_layout)
    RelativeLayout playAllSongLayout;
    @BindView(R.id.recycler_complete_download)
    RecyclerView recyclerCompleteDownload;
    Unbinder unbinder;

    private OkDownload okDownload;
    private DownloadAdapter downloadAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DownloadCompleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.compositeDisposable.add(RxBus.getInstance().register(DownloadCompleteEvent.class).subscribe(new Consumer<DownloadCompleteEvent>() {
            @Override
            public void accept(DownloadCompleteEvent downloadCompleteEvent) throws Exception {
                onEvent(downloadCompleteEvent);
            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_complete, container, false);
        unbinder = ButterKnife.bind(this, view);
        okDownload = OkDownload.getInstance();
        downloadAdapter = new DownloadAdapter(getActivity());
        downloadAdapter.updateData(1);
        recyclerCompleteDownload.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCompleteDownload.setAdapter(downloadAdapter);
        okDownload.addOnAllTaskEndListener(this);
        return view;
    }

    public void onEvent(DownloadCompleteEvent downloadCompleteEvent) {
        this.downloadAdapter.updateData(1);
    }

    @Override
    public void onAllTaskEnd() {
        showSnackBar("所有下载任务已结束");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        okDownload.removeOnAllTaskEndListener(this);
        downloadAdapter.unRegister();
    }
}
