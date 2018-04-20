package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.SearchSongInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchSongRecyclerAdapter extends RecyclerView.Adapter<SearchSongRecyclerAdapter.SearchSongRecyclerViewHolder> {

    private Context mContext;
    private ArrayList<SearchSongInfo> songInfoList;


    public SearchSongRecyclerAdapter(Context context, ArrayList<SearchSongInfo> searchSongInfos) {
        this.mContext = context;
        this.songInfoList = searchSongInfos;
    }

    @Override
    public SearchSongRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_music_listitem, parent, false);
        return new SearchSongRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchSongRecyclerViewHolder holder, int position) {
        final SearchSongInfo song = songInfoList.get(position);
        holder.songTitle.setText(Html.fromHtml(song.getTitle()));
        if (TextUtils.isEmpty(song.getAuthor())) {
            holder.songArtist.setVisibility(View.GONE);
        } else {
            holder.songArtist.setVisibility(View.VISIBLE);
            holder.songArtist.setText(Html.fromHtml(song.getAuthor()));
        }
        int number = position + 1;
        holder.songNumber.setText(String.valueOf(number));
        if (onSongClickListener != null) {
            holder.musicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onSongClickListener.onSongClick(holder.musicItem, pos, song.getSong_id());
                }
            });
            holder.songSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSongClickListener.onSongSettingsClick(view, holder.getAdapterPosition(), songInfoList);
                }
            });
        }
    }

    public void setData(ArrayList<SearchSongInfo> songInfos) {
        this.songInfoList = songInfos;
    }

    public interface OnSongClickListener {
        void onSongClick(View view, int position, String songId);
        void onSongSettingsClick(View view, int position, ArrayList<SearchSongInfo> searchSongInfos);
    }

    private OnSongClickListener onSongClickListener;

    public void setOnSongClickListener(OnSongClickListener onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }

    @Override
    public int getItemCount() {
        return (songInfoList != null ? songInfoList.size() : 0);
    }

    public static class SearchSongRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.song_number)
        TextView songNumber;
        @BindView(R.id.song_playing_icon)
        ImageView songPlayingIcon;
        @BindView(R.id.song_header_layout)
        FrameLayout songHeaderLayout;
        @BindView(R.id.song_settings)
        AppCompatImageView songSettings;
        @BindView(R.id.song_title)
        TextView songTitle;
        @BindView(R.id.song_artist)
        TextView songArtist;
        @BindView(R.id.music_item)
        RelativeLayout musicItem;

        public SearchSongRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
