package com.fatwong.encore.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.PlayQueueRecyclerAdapter;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.interfaces.OnSongChangeListener;
import com.fatwong.encore.service.MusicPlayerManager;
import com.lb.materialdesigndialog.base.DialogBase;
import com.lb.materialdesigndialog.base.DialogWithTitle;
import com.lb.materialdesigndialog.impl.MaterialDialogNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayQueueFragment extends DialogFragment implements OnSongChangeListener {

    @BindView(R.id.play_queue_add)
    TextView playQueueAdd;
    @BindView(R.id.play_queue_number)
    TextView playQueueNumber;
    @BindView(R.id.play_queue_clear_all)
    TextView playQueueClearAll;
    @BindView(R.id.play_queue_recycler)
    RecyclerView playQueueRecycler;
    Unbinder unbinder;

    private Context mContext;
    private PlayQueueRecyclerAdapter playQueueAdapter;

    public PlayQueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);
        View view = inflater.inflate(R.layout.fragment_play_queue, container, false);
        unbinder = ButterKnife.bind(this, view);
        MusicPlayerManager.get().registerOnSongChangedListener(this);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view) {
        playQueueAdapter = new PlayQueueRecyclerAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        playQueueRecycler.setLayoutManager(layoutManager);
        playQueueRecycler.setHasFixedSize(true);
        playQueueRecycler.setAdapter(playQueueAdapter);
        playQueueRecycler.setItemAnimator(new DefaultItemAnimator());

        if (MusicPlayerManager.get().getPlayList() != null) {
            playQueueAdapter.setSongs(MusicPlayerManager.get().getPlayList().getQueue());
        }

        playQueueAdapter.setSongClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Object item, int position) {
                MusicPlayerManager.get().playQueueItem(position);
            }

            @Override
            public void onItemSettingClick(View view, Object item, int position) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置PlayQueueFragment高度与宽度
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.6);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);
    }

    @Override
    public void onSongChanged(Song song) {
        if (MusicPlayerManager.get().getPlayList() != null) {
            playQueueAdapter.setSongs(MusicPlayerManager.get().getPlayList().getQueue());
        }
    }

    @Override
    public void onPlayBackStateChanged(PlaybackStateCompat stateCompat) {

    }

    private void showClearAllDialog() {
        MaterialDialogNormal materialDialogNormal = new MaterialDialogNormal(getActivity());
        materialDialogNormal.setMessage("Are you sure to clear the playlist?");
        materialDialogNormal.setNegativeButton("Cancel", new DialogWithTitle.OnClickListener() {
            @Override
            public void click(DialogBase dialog, View view) {
                dialog.dismiss();
            }
        });
        materialDialogNormal.setPositiveButton("Confirm", new DialogWithTitle.OnClickListener() {
            @Override
            public void click(DialogBase dialog, View view) {
                playQueueAdapter.clearAllSongs();
                MusicPlayerManager.get().clearPlayList();
                dismiss();
            }
        });
        materialDialogNormal.setTitle("Clear Play Queue");
    }

    @OnClick({R.id.play_queue_add, R.id.play_queue_clear_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.play_queue_add:
                break;
            case R.id.play_queue_clear_all:
                showClearAllDialog();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        MusicPlayerManager.get().unRegisterOnSongChangedListener(this);
    }
}
