package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistDetailRecyclerAdapter extends RecyclerView.Adapter<PlaylistDetailRecyclerAdapter.PlaylistCommonItemViewHolder> {

    private Context mContext;
    private List<Song> songs;
    private OnItemClickListener<Song> onSongClickListener;

    public PlaylistDetailRecyclerAdapter(Context context) {
        this.mContext = context;
        songs = new ArrayList<>();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @Override
    public PlaylistCommonItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaylistCommonItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.playlist_detail_listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaylistCommonItemViewHolder holder, int position) {
        final Song item = songs.get(position);
        holder.playlistSongTrackNumber.setText(position + 1 + "");
        holder.playlistSongTitle.setText(item.getTitle());
        holder.playlistSongArtist.setText(item.getArtistName());

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public OnItemClickListener<Song> getOnSongClickListener() {
        return onSongClickListener;
    }

    public void setOnSongClickListener(OnItemClickListener<Song> onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }


    public class PlaylistCommonItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.playlist_song_item)
        RelativeLayout playlistSongItem;
        @BindView(R.id.playlist_song_track_number)
        TextView playlistSongTrackNumber;
        @BindView(R.id.playlist_song_title)
        TextView playlistSongTitle;
        @BindView(R.id.playlist_song_artist)
        TextView playlistSongArtist;
        @BindView(R.id.playlist_song_settings)
        ImageView playlistSongSettings;

        public PlaylistCommonItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            playlistSongItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemClick(song, getAdapterPosition());
                    }
                }
            });
            playlistSongSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemSettingClick(playlistSongSettings, song, getAdapterPosition());
                    }
                }
            });
        }
    }
}
