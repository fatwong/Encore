package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaac on 28/3/2018.
 */

public class RecentlyPlayedRecyclerAdapter extends RecyclerView.Adapter<RecentlyPlayedRecyclerAdapter.RecentlyPlayedViewHolder> {

    private List<Song> songs;
    private Context mContext;
    private OnItemClickListener<Song> onSongClickListener;

    public RecentlyPlayedRecyclerAdapter(Context context) {
        this.mContext = context;
        songs = new ArrayList<>();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @Override
    public RecentlyPlayedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recently_played_listitem, parent, false);
        return new RecentlyPlayedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentlyPlayedViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.recentlyPlayedSongTitle.setText(Html.fromHtml(song.getTitle()));
        if (TextUtils.isEmpty(song.getArtistName())) {
            holder.recentlyPlayedSongArtist.setText(R.string.music_unknown);
        } else {
            holder.recentlyPlayedSongArtist.setText(song.getArtistName());
        }
        Glide.with(mContext)
                .load(song.getCoverUrl())
                .apply(new RequestOptions().placeholder(R.drawable.cover))
                .into(holder.recentlyPlayedSongCover);
    }

    public OnItemClickListener getOnSongClickListener() {
        return onSongClickListener;
    }

    public void setOnSongClickListener(OnItemClickListener<Song> onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class RecentlyPlayedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recently_played_song_cover)
        ImageView recentlyPlayedSongCover;
        @BindView(R.id.recently_played_song_setting)
        AppCompatImageView recentlyPlayedSongSetting;
        @BindView(R.id.recently_played_song_title)
        TextView recentlyPlayedSongTitle;
        @BindView(R.id.recently_played_song_artist)
        TextView recentlyPlayedSongArtist;
        @BindView(R.id.recently_played_song_item)
        RelativeLayout recentlyPlayedSongItem;

        public RecentlyPlayedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            recentlyPlayedSongSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemSettingClick(recentlyPlayedSongSetting, song, getAdapterPosition());
                    }
                }
            });
            recentlyPlayedSongItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemClick(song, getAdapterPosition());
                    }
                }
            });
        }
    }
}
