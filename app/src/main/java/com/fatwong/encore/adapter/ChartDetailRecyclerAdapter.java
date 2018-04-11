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
import com.fatwong.encore.bean.ChartInfo;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.interfaces.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartDetailRecyclerAdapter extends RecyclerView.Adapter<ChartDetailRecyclerAdapter.ChartDetailRecyclerViewHolder> {

    private Context mContext;
    private ChartInfo info;
    private long playingId;

    public ChartDetailRecyclerAdapter(Context context) {
        mContext = context;
        notifyDataSetChanged();
    }

    public void setData(ChartInfo info) {
        this.info = info;
    }

    @Override
    public ChartDetailRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_music_listitem, parent, false);
        return new ChartDetailRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChartDetailRecyclerViewHolder holder, int position) {
        final ChartInfo.SongListBean song = info.getSong_list().get(position);
        holder.songTitle.setText(song.getTitle());
        if (TextUtils.isEmpty(song.getAuthor())) {
            holder.songArtist.setVisibility(View.GONE);
        } else {
            holder.songArtist.setVisibility(View.VISIBLE);
            holder.songArtist.setText(song.getAuthor());
        }
        int number = position + 1;
        holder.songNumber.setText(String.valueOf(number));
        if (onSongClickListener != null) {
            holder.musicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onSongClickListener.onSongClick(holder.itemView, pos, song.getSong_id());
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

    @Override
    public int getItemCount() {
        return info.getSong_list().size();
    }

    public long getPlayingId() {
        return playingId;
    }

    public void setPlayingId(long playingId) {
        this.playingId = playingId;
    }

    public interface OnSongClickListener
    {
        void onSongClick(View view, int position, String songId);
        void onSongSettingsClick(View view, int position, ChartInfo chartInfo);
    }

    private OnSongClickListener onSongClickListener;

    public void setOnItemClickLitener(OnSongClickListener onSongClickListener) {
        this.onSongClickListener = onSongClickListener;
    }

    public class ChartDetailRecyclerViewHolder extends RecyclerView.ViewHolder {
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

        public ChartDetailRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
