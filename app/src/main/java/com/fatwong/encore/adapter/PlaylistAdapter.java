package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
        playlists.add(createDefaultPlaylist());
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
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_playlist_listitem, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        final Playlist playlist = playlists.get(position);
        if (playlist.getId() == -1) {
            holder.collectionSetting.setVisibility(View.GONE);
            holder.collectionCount.setVisibility(View.GONE);
            holder.collectionTitle.setText(R.string.collection_create);
            holder.collectionCover.setImageResource(R.drawable.ah1);
            holder.collectionItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPlaylistClickListener != null) {
                        onPlaylistClickListener.onItemClick(null, -1);
                    }
                    CreatePlaylistActivity.open(context);
                }
            });
        } else {
            holder.collectionSetting.setVisibility(View.VISIBLE);
            holder.collectionCount.setVisibility(View.VISIBLE);
            holder.collectionTitle.setText(playlist.getTitle());
            String count = String.format(context.getString(R.string.song), playlist.getCount());
            holder.collectionCount.setText(count);
            Glide.with(context)
                    .load(playlist.getCoverUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.cover))
                    .into(holder.collectionCover);
        }
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


    @OnClick({R.id.collection_cover, R.id.collection_setting, R.id.collection_title, R.id.collection_count, R.id.collection_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.collection_cover:
                break;
            case R.id.collection_setting:
                break;
            case R.id.collection_title:
                break;
            case R.id.collection_count:
                break;
        }
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.collection_cover)
        ImageView collectionCover;
        @BindView(R.id.collection_setting)
        AppCompatImageView collectionSetting;
        @BindView(R.id.collection_title)
        TextView collectionTitle;
        @BindView(R.id.collection_count)
        TextView collectionCount;
        @BindView(R.id.collection_item)
        RelativeLayout collectionItem;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
