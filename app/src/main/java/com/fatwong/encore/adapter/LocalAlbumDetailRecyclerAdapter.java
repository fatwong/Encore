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

public class LocalAlbumDetailRecyclerAdapter extends RecyclerView.Adapter<LocalAlbumDetailRecyclerAdapter.LocalAlbumViewHolder> {
    private Context mContext;
    private List<Song> songs;
    private OnItemClickListener<Song> onSongClickListener;

    public LocalAlbumDetailRecyclerAdapter(Context context) {
        this.mContext = context;
        songs = new ArrayList<>();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }


    @Override
    public LocalAlbumDetailRecyclerAdapter.LocalAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.local_music_listitem, parent, false);
        return new LocalAlbumDetailRecyclerAdapter.LocalAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalAlbumDetailRecyclerAdapter.LocalAlbumViewHolder holder, final int position) {
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

    public OnItemClickListener<Song> getOnSongClickListener() {
        return onSongClickListener;
    }

    public void setOnSongClickListener(OnItemClickListener<Song> onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }

    public class LocalAlbumViewHolder extends RecyclerView.ViewHolder {

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

        public LocalAlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            localSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemSettingClick(localSettings, song, getAdapterPosition());
                    }
                }
            });
            localRecyclerItem.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
