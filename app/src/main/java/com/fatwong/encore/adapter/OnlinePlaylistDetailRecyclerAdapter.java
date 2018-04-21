package com.fatwong.encore.adapter;


import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.OnlinePlaylistInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnlinePlaylistDetailRecyclerAdapter extends RecyclerView.Adapter<OnlinePlaylistDetailRecyclerAdapter.OnlinePlaylistDetailViewHolder> {


    private Context mContext;
    private OnlinePlaylistInfo info;

    public OnlinePlaylistDetailRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public OnlinePlaylistDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_music_listitem, parent, false);
        return new OnlinePlaylistDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OnlinePlaylistDetailViewHolder holder, int position) {
        final OnlinePlaylistInfo.ContentBean song = info.getContent().get(position);
        holder.songTitle.setText(song.getTitle());
        if (TextUtils.isEmpty(song.getAuthor())) {
            holder.songArtist.setVisibility(View.GONE);
        } else {
            holder.songArtist.setVisibility(View.VISIBLE);
            holder.songArtist.setText(song.getAuthor());
        }
        int number = position + 1;
        holder.songNumber.setText(String.valueOf(number));

        //TODO 设置音乐的状态


        // 如果设置了回调，则设置点击事件
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
                    onSongClickListener.onSongSettingsClick(view, holder.getAdapterPosition(), info);
                }
            });
        }
    }

    public void setData(OnlinePlaylistInfo info) {
        this.info = info;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return info != null ? info.getContent().size() : 0;
    }

    public interface OnSongClickListener {
        void onSongClick(View view, int position, String songId);
        void onSongSettingsClick(View view, int position, OnlinePlaylistInfo onlinePlaylistInfo);
    }

    private OnSongClickListener onSongClickListener;

    public void setOnSongClickListener(OnSongClickListener onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }

    public class OnlinePlaylistDetailViewHolder extends RecyclerView.ViewHolder {
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

        public OnlinePlaylistDetailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

