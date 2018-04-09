package com.fatwong.encore.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.db.PlaylistManager;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.ui.activity.CreatePlaylistActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private OnItemClickListener<Playlist> onPlaylistClickListener;

    public PlaylistAdapter(Context context) {
        this.context = context;
        updatePlaylist();
    }

    public void updatePlaylist() {
        playlists = PlaylistManager.getInstance().getAllPlaylists();
        for (Playlist playlist: playlists) {
            if (playlist.getId() == -1) {
                playlists.remove(playlist);
            }
        }
//        playlists.add(createDefaultPlaylist());
        notifyDataSetChanged();
    }

    public void deletePlaylist(Playlist playlist) {
        if (playlists.contains(playlist)) {
            playlists.remove(playlist);
        }
        notifyDataSetChanged();
    }

    private Playlist createDefaultPlaylist() {
        Playlist playlist = new Playlist();
        playlist.setId(-1);
        return playlist;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_listitem, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlaylistViewHolder holder, int position) {
        final Playlist playlist = playlists.get(position);
            holder.playlistSetting.setVisibility(View.VISIBLE);
            holder.playlistSongCount.setVisibility(View.VISIBLE);
            holder.playlistTitle.setText(playlist.getTitle());
            String count = String.format(context.getString(R.string.song), playlist.getCount());
            holder.playlistSongCount.setText(count);
            Glide.with(context)
                    .load(playlist.getCoverUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.cover))
                    .into(holder.playlistCover);
            holder.playlistItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPlaylistClickListener != null) {
                        onPlaylistClickListener.onItemClick(playlist, holder.getAdapterPosition());
                    }
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        return;
                    }
                }
            });
            holder.playlistSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPlaylistClickListener != null) {
                        onPlaylistClickListener.onItemSettingClick(holder.playlistSetting, playlist, holder.getAdapterPosition());
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public OnItemClickListener<Playlist> getOnPlaylistClickListener() {
        return onPlaylistClickListener;
    }

    public void setOnPlaylistClickListener(OnItemClickListener<Playlist> onPlaylistClickListener) {
        this.onPlaylistClickListener = onPlaylistClickListener;
    }


    @OnClick({R.id.playlist_cover, R.id.playlist_setting, R.id.playlist_title, R.id.playlist_song_count, R.id.playlist_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.playlist_cover:
                break;
            case R.id.playlist_setting:
                break;
            case R.id.playlist_title:
                break;
            case R.id.playlist_song_count:
                break;
        }
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.playlist_cover)
        ImageView playlistCover;
        @BindView(R.id.playlist_setting)
        AppCompatImageView playlistSetting;
        @BindView(R.id.playlist_title)
        TextView playlistTitle;
        @BindView(R.id.playlist_song_count)
        TextView playlistSongCount;
        @BindView(R.id.playlist_item)
        RelativeLayout playlistItem;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
