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
 * Created by Isaac on 2018/3/7.
 */

public class LocalMusicRecyclerAdapter extends RecyclerView.Adapter<LocalMusicRecyclerAdapter.LocalMusicViewHolder> {

    private Context mContext;
    private List<Song> songs;
    private OnItemClickListener<Song> itemClickListener;

    public LocalMusicRecyclerAdapter(Context context) {
        this.mContext = context;
        songs = new ArrayList<>();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }


    @Override
    public LocalMusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.local_music_listitem, parent, false);
        return new LocalMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalMusicViewHolder holder, final int position) {
        final Song song = songs.get(position);
        holder.localSongTitle.setText(Html.fromHtml(song.getTitle()));
        if (TextUtils.isEmpty(song.getArtistName())) {
            holder.localSongArtist.setText("Unknown");
        } else {
            holder.localSongArtist.setText(song.getArtistName());
        }
        Glide.with(mContext)
                .load(song.getCoverUrl())
                .apply(new RequestOptions().placeholder(R.drawable.cover))
                .into(holder.localSongCover);
    }

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class LocalMusicViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.local_song_cover)
        ImageView localSongCover;
        @BindView(R.id.local_settings)
        AppCompatImageView localSettings;
        @BindView(R.id.local_song_title)
        TextView localSongTitle;
        @BindView(R.id.local_song_artist)
        TextView localSongArtist;
        @BindView(R.id.local_recycler_item)
        RelativeLayout localRecyclerItem;

        public LocalMusicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            localSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (itemClickListener != null && song.isStatus()) {
                        itemClickListener.onItemSettingClick(localSettings, song, getAdapterPosition());
                    }
                }
            });
            localRecyclerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (itemClickListener != null && song.isStatus()) {
                        itemClickListener.onItemClick(song, getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
