package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.ItemTouchHelperAdapter;
import com.fatwong.encore.interfaces.ItemTouchHelperViewHolder;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.service.MusicPlayerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isaac on 30/3/2018.
 */

public class PlayQueueRecyclerAdapter extends RecyclerView.Adapter<PlayQueueRecyclerAdapter.PlayListHolder> implements ItemTouchHelperAdapter {

    private List<Song> songs;
    private Context mContext;
    private OnItemClickListener<Song> onSongClickListener;
    private int currentPlayingPosition;

    public PlayQueueRecyclerAdapter(Context context) {
        this.mContext = context;
        songs = new ArrayList<>();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    public void clearAllSongs() {
        int itemCount = getItemCount();
        songs.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Override
    public PlayListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.play_queue_listitem, parent, false);
        return new PlayListHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayListHolder holder, int position) {
        final Song song = songs.get(position);
        holder.playQueueSongName.setText(Html.fromHtml(song.getTitle()));
        if (TextUtils.isEmpty(song.getArtistName())) {
            holder.playQueueSongArtist.setVisibility(View.GONE);
        } else {
            holder.playQueueSongArtist.setVisibility(View.VISIBLE);
            holder.playQueueSongArtist.setText(song.getArtistName());
        }
        if (MusicPlayerManager.get().getCurrentSong() != null && song.getId() == MusicPlayerManager.get().getCurrentSong().getId()) {
            holder.playQueueSongName.setTextColor(mContext.getResources().getColor(R.color.theme_color_primary));
        } else {
            holder.playQueueSongName.setTextColor(mContext.getResources().getColor(R.color.black_normal));
        }
        if (MusicPlayerManager.get().getCurrentSong().getId() == song.getId()) {
            holder.playQueueSongState.setVisibility(View.VISIBLE);
            holder.playQueueSongState.setImageResource(R.drawable.song_play_icon);
            holder.playQueueSongState.setImageTintList(R.color.theme_color_primary);
            currentPlayingPosition = position;
        } else {
            holder.playQueueSongState.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public OnItemClickListener getOnSongClickListener() {
        return onSongClickListener;
    }

    public void setSongClickListener(OnItemClickListener onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(songs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        songs.remove(position);
        notifyItemRemoved(position);
    }

    public class PlayListHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
        @BindView(R.id.play_queue_song_state)
        TintImageView playQueueSongState;
        @BindView(R.id.play_queue_song_name)
        TextView playQueueSongName;
        @BindView(R.id.play_queue_song_artist)
        TextView playQueueSongArtist;
        @BindView(R.id.play_queue_song_delete)
        ImageView playQueueSongDelete;
        @BindView(R.id.play_queue_song_layout)
        RelativeLayout playQueueSongLayout;

        public PlayListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            playQueueSongLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemClick(song, getAdapterPosition());
                    }
                }
            });
            playQueueSongDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Song song = songs.get(getAdapterPosition());
                    if (onSongClickListener != null && song.isStatus()) {
                        onSongClickListener.onItemSettingClick(playQueueSongDelete, song, getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }
}
