package com.fatwong.encore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import com.fatwong.encore.adapter.PopupRecyclerAdapter;
import com.fatwong.encore.bean.ChartInfo;
import com.fatwong.encore.bean.OnlinePlaylistInfo;
import com.fatwong.encore.bean.OverFlowItem;
import com.fatwong.encore.bean.SearchSongInfo;
import com.fatwong.encore.bean.SongInfo;
import com.fatwong.encore.interfaces.ICallback;
import com.fatwong.encore.net.LogDownloadListener;
import com.fatwong.encore.utils.HandlerUtils;
import com.fatwong.encore.utils.OkGoUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DownloadPopupFragment extends DialogFragment {
    @BindView(R.id.popup_list_title)
    TextView popupListTitle;
    @BindView(R.id.popup_list)
    RecyclerView popupList;
    Unbinder unbinder;
    @BindView(R.id.pop_layout)
    LinearLayout popLayout;
    private Context mContext;
    private Handler mHandler;
    private LinearLayoutManager layoutManager;
    private List<OverFlowItem> popupItemList = new ArrayList<>();  //声明一个list，动态存储要显示的信息
    private PopupRecyclerAdapter popupRecyclerAdapter;
    private int position;
    private OnlinePlaylistInfo playlistInfo;
    private OnlinePlaylistInfo.ContentBean playlistInfoContentBean;
    private ChartInfo chartInfo;
    private ChartInfo.SongListBean songListBean;
    private ArrayList<SearchSongInfo> searchSongInfos;


    public DownloadPopupFragment() {
        // Required empty public constructor
    }

    public static DownloadPopupFragment newInstance(OnlinePlaylistInfo info, Context context, int position) {
        DownloadPopupFragment fragment = new DownloadPopupFragment();
        fragment.playlistInfo = info;
        fragment.mContext = context;
        fragment.position = position;
        return fragment;
    }

    public static DownloadPopupFragment newInstance(ChartInfo info, Context context, int position) {
        DownloadPopupFragment fragment = new DownloadPopupFragment();
        fragment.chartInfo = info;
        fragment.mContext = context;
        fragment.position = position;
        return fragment;
    }

    public static DownloadPopupFragment newInstance(ArrayList<SearchSongInfo> songInfos, Context context, int position) {
        DownloadPopupFragment fragment = new DownloadPopupFragment();
        fragment.searchSongInfos = songInfos;
        fragment.mContext = context;
        fragment.position = position;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置样式
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

        View view = inflater.inflate(R.layout.fragment_popup, container, false);
        unbinder = ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(mContext);
        popupList.setLayoutManager(layoutManager);
        popupList.setHasFixedSize(true);
        setPopupList();
        popupRecyclerAdapter = new PopupRecyclerAdapter(getActivity(), popupItemList);
        popupList.setAdapter(popupRecyclerAdapter);
        popupRecyclerAdapter.setOnItemClickListener(new PopupRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                switch (pos) {
                    case 0:
                        break;
                    case 1:
                        String songId = getCurrentSongId();
                        OkGoUtils.getInstance().getSongInfo(songId, new ICallback() {
                            @Override
                            public void getSongInfoSuccess(SongInfo songInfo) {
                                String fileLink = songInfo.getBitrate().getFile_link();
                                GetRequest<File> request = OkGo.<File>get(fileLink);
                                OkDownload.request(songInfo.getSonginfo().getSong_id(), request)
                                        .fileName(songInfo.getSonginfo().getTitle() + ".mp3")
                                        .save()
                                        .register(new LogDownloadListener())
                                        .start();
                            }

                            @Override
                            public void getSongInfoFailed(Throwable e) {
                            }
                        });
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

    public String getCurrentSongId() {
        if (playlistInfo != null) {
            return playlistInfo.getContent().get(position).getSong_id();
        } else if (chartInfo != null) {
            return chartInfo.getSong_list().get(position).getSong_id();
        } else {
            return searchSongInfos.get(position).getSong_id();
        }
    }

    public String getCurrentSongAuthor() {
        if (playlistInfo != null) {
            return playlistInfo.getContent().get(position).getAuthor();
        } else if (chartInfo != null) {
            return chartInfo.getSong_list().get(position).getAuthor();
        } else {
            return Html.fromHtml(searchSongInfos.get(position).getAuthor()).toString();
        }
    }

    public String getCurrentSongAlbumTitle() {
        if (playlistInfo != null) {
            return playlistInfo.getContent().get(position).getAlbum_title();
        } else if (chartInfo != null) {
            return chartInfo.getSong_list().get(position).getAlbum_title();
        } else {
            return searchSongInfos.get(position).getAlbum_title();
        }
    }



//    private void showPlaylistDialog(final Song song) {
//        PlaylistRecyclerAdapter playlistRecyclerAdapter = new PlaylistRecyclerAdapter(mContext);
//        final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
//                .title(R.string.collection_dialog_selection_title)
//                .adapter(playlistRecyclerAdapter, new LinearLayoutManager(mContext))
//                .build();
//        playlistRecyclerAdapter.setOnPlaylistClickListener(new OnItemClickListener<Playlist>() {
//            @Override
//            public void onItemClick(Playlist item, int position) {
//                if (item == null) {
//                    dialog.dismiss();
//                    return;
//                }
//                PlaylistManager.getInstance().insertPlaylistRelationAsync(item, song, new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        dialog.dismiss();
//                        Toast.makeText(mContext, aBoolean ? R.string.collect_song_success : R.string.collect_song_fail, Toast.LENGTH_SHORT).show();
//                        RxBus.getInstance().post(new UpdatePlaylistEvent(aBoolean));
//                    }
//                });
//            }
//
//            @Override
//            public void onItemSettingClick(View view, Playlist item, int position) {
//
//            }
//        });
//        dialog.show();
//    }

    private void setPopupList() {
        setPopupItem("收藏到歌单", R.drawable.ic_red_addcollection);
        setPopupItem("下载",R.drawable.ic_red_download);
        setPopupItem("歌手: " + getCurrentSongAuthor(), R.drawable.ic_red_singer);
        setPopupItem("专辑: " + getCurrentSongAlbumTitle(), R.drawable.ic_red_album);
        setPopupItem("分享", R.drawable.ic_red_share);
    }

    private void setItemDecoration() {
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        popupList.addItemDecoration(itemDecoration);
    }

    private void setPopupItem(String title, int avatarId) {
        OverFlowItem item = new OverFlowItem();
        item.setTitle(title);
        item.setAvatar(avatarId);
        popupItemList.add(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
