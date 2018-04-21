package com.fatwong.encore.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.PlaylistRecyclerAdapter;
import com.fatwong.encore.adapter.PopupRecyclerAdapter;
import com.fatwong.encore.bean.OverFlowItem;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.db.PlaylistManager;
import com.fatwong.encore.ui.activity.CreatePlaylistActivity;
import com.fatwong.encore.ui.activity.PlaylistDetailActivity;
import com.fatwong.encore.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistPopupFragment extends DialogFragment {

    public static final int TYPE_DOWNLOAD = 0;  //说明是带有Header的
    public static final int TYPE_SHARE = 1;  //说明是带有Footer的
    public static final int TYPE_EDITOR = 2;  //说明是不带有header和footer的
    public static final int TYPE_DELETE = 3;  //说明是不带有header和footer的

    @BindView(R.id.popup_list_title)
    TextView popupListTitle;
    @BindView(R.id.popup_list)
    RecyclerView popupList;
    @BindView(R.id.pop_layout)
    LinearLayout popLayout;
    Unbinder unbinder;

    private Context mContext;
    private Handler mHandler;
    private LinearLayoutManager layoutManager;
    private List<OverFlowItem> overFlowItemList = new ArrayList<>();  //声明一个list，动态存储要显示的信息
    private PopupRecyclerAdapter popupRecyclerAdapter;
    private PlaylistRecyclerAdapter playlistRecyclerAdapter;
    private Playlist currentPlaylist;
    private int currentPosition;

    public PlaylistPopupFragment() {
        // Required empty public constructor
    }

    public static PlaylistPopupFragment newInstance(int position, Playlist playlist, PlaylistRecyclerAdapter playlistRecyclerAdapter) {
        PlaylistPopupFragment fragment = new PlaylistPopupFragment();
        fragment.currentPosition = position;
        fragment.currentPlaylist = playlist;
        fragment.playlistRecyclerAdapter = playlistRecyclerAdapter;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDatePickerDialog);
        mContext = getContext();
        mHandler = HandlerUtils.getInstance(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //设置无标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置从底部弹出
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setAttributes(params);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popup, container, false);
        unbinder = ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(mContext);
        popupList.setLayoutManager(layoutManager);
        popupList.setHasFixedSize(true);
        setPopupList();
        popupRecyclerAdapter = new PopupRecyclerAdapter(getActivity(), overFlowItemList);
        popupList.setAdapter(popupRecyclerAdapter);
        popupRecyclerAdapter.setOnItemClickListener(new PopupRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case TYPE_DOWNLOAD:
                        break;
                    case TYPE_SHARE:
                        break;
                    case TYPE_EDITOR:
                        CreatePlaylistActivity.open(getActivity(), currentPlaylist.getId());
                        break;
                    case TYPE_DELETE:
                        PlaylistManager.getInstance().deletePlaylist(currentPlaylist);
                        playlistRecyclerAdapter.deletePlaylist(currentPlaylist);
                        break;
                }
                dismiss();
            }
        });
        setItemDecoration();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewTreeObserver observer = popLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                popLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int measuredHeight = popLayout.getMeasuredHeight();
                getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, measuredHeight);
                getDialog().setCanceledOnTouchOutside(true);
            }
        });
    }
    private void setItemDecoration() {
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        popupList.addItemDecoration(itemDecoration);
    }

    private void setPopupList() {
        setPopupItem("下载", R.drawable.ic_red_download);
        setPopupItem("编辑歌单信息", R.drawable.ic_red_editor);
        setPopupItem("删除", R.drawable.ic_red_delete);
    }

    private void setPopupItem(String title, int avatarId) {
        OverFlowItem item = new OverFlowItem();
        item.setTitle(title);
        item.setAvatar(avatarId);
        overFlowItemList.add(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
