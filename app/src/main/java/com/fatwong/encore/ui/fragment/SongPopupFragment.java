package com.fatwong.encore.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fatwong.encore.R;
import com.fatwong.encore.adapter.PlaylistDetailRecyclerAdapter;
import com.fatwong.encore.adapter.PlaylistRecyclerAdapter;
import com.fatwong.encore.adapter.PopupRecyclerAdapter;
import com.fatwong.encore.bean.Album;
import com.fatwong.encore.bean.Artist;
import com.fatwong.encore.bean.OverFlowItem;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.db.PlaylistManager;
import com.fatwong.encore.event.UpdatePlaylistEvent;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.ui.activity.LocalAlbumDetailActivity;
import com.fatwong.encore.ui.activity.LocalArtistDetailActivity;
import com.fatwong.encore.ui.activity.LocalMusicActivity;
import com.fatwong.encore.utils.HandlerUtils;
import com.fatwong.encore.utils.LocalMusicLibrary;
import com.fatwong.encore.utils.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongPopupFragment extends DialogFragment {

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
    private Song currentSong;
    private Playlist currentPlaylist;
    private PlaylistDetailRecyclerAdapter detailRecyclerAdapter;


    public SongPopupFragment() {
        // Required empty public constructor
    }

    public static SongPopupFragment newInstance(Song song, Context context) {
        SongPopupFragment fragment = new SongPopupFragment();
        fragment.currentSong = song;
        fragment.mContext = context;
        return fragment;
    }

    public static SongPopupFragment newInstance(Song song, Context context, Playlist playlist, PlaylistDetailRecyclerAdapter adapter) {
        SongPopupFragment fragment = new SongPopupFragment();
        fragment.currentSong = song;
        fragment.mContext = context;
        fragment.currentPlaylist = playlist;
        fragment.detailRecyclerAdapter = adapter;
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
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        showPlaylistDialog(currentSong);
                        break;
                    case 1:
                        Artist artist = LocalMusicLibrary.getArtist(getContext(), currentSong.getArtistId());
                        LocalArtistDetailActivity.open(getContext(), artist);
                        break;
                    case 2:
                        Album album = LocalMusicLibrary.getAlbum(getContext(), currentSong.getAlbumId());
                        LocalAlbumDetailActivity.open(getContext(), album);
                        break;
                    case 3:
                        startActivity(new Intent(getActivity(), LocalMusicActivity.class));
                        break;
                    case 4:
                        if (currentPlaylist != null) {
                            PlaylistManager.getInstance().deletePlaylistRelation(currentPlaylist.getId(), (int) currentSong.getId());
                            detailRecyclerAdapter.removeSong(currentSong);
                        }
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

    private void showPlaylistDialog(final Song song) {
        PlaylistRecyclerAdapter playlistRecyclerAdapter = new PlaylistRecyclerAdapter(mContext);
        final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .title(R.string.collection_dialog_selection_title)
                .adapter(playlistRecyclerAdapter, new LinearLayoutManager(mContext))
                .build();
        playlistRecyclerAdapter.setOnPlaylistClickListener(new OnItemClickListener<Playlist>() {
            @Override
            public void onItemClick(Playlist item, int position) {
                if (item == null) {
                    dialog.dismiss();
                    return;
                }
                PlaylistManager.getInstance().insertPlaylistRelationAsync(item, song, new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        dialog.dismiss();
                        Toast.makeText(mContext, aBoolean ? R.string.collect_song_success : R.string.collect_song_fail, Toast.LENGTH_SHORT).show();
                        RxBus.getInstance().post(new UpdatePlaylistEvent(aBoolean));
                    }
                });
            }

            @Override
            public void onItemSettingClick(View view, Playlist item, int position) {

            }
        });
        dialog.show();
    }

    private void setPopupList() {
        boolean status = currentSong.isStatus();
        setPopupItem("收藏到歌单", R.drawable.ic_red_addcollection);
        setPopupItem("歌手: " + currentSong.getArtistName(), R.drawable.ic_red_singer);
        setPopupItem("专辑: " + currentSong.getAlbumName(), R.drawable.ic_red_album);
        if(status){
            setPopupItem("来源: 本地音乐", R.drawable.ic_red_resource);
        }else
            setPopupItem("来源: 网络歌曲", R.drawable.ic_red_resource);
        if (currentPlaylist != null) {
            setPopupItem("删除", R.drawable.ic_red_delete);
        }
        if (currentSong.getQuality() != null) {
            setPopupItem("音质: " + currentSong.getQuality(), R.drawable.ic_red_quality);
        }
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
