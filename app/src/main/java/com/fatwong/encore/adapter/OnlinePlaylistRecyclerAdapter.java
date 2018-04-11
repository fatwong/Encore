package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.OnlinePlaylist;
import com.fatwong.encore.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnlinePlaylistRecyclerAdapter extends RecyclerView.Adapter<OnlinePlaylistRecyclerAdapter.OnlinePlaylistViewHolder> {

    private Context mContext;
    private List<OnlinePlaylist.ContentBean> resultData = new ArrayList<>();

    public OnlinePlaylistRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public OnlinePlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.online_playlist_listitem, parent, false);
        return new OnlinePlaylistViewHolder(view);
    }

    public void setData(List<OnlinePlaylist.ContentBean> data) {
        this.resultData = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final OnlinePlaylistViewHolder holder, int position) {
        final OnlinePlaylist.ContentBean dataBean = resultData.get(position);
        holder.playlistListenerCount.setText("" + dataBean.getListenum());
        holder.playlistSubtitle.setText(dataBean.getTag());
        holder.playlistTitle.setText(dataBean.getTitle());
        ImageUtils.glideWith(mContext, dataBean.getPic_300(), R.drawable.a8y, holder.playlistAvatar);
        if (onPlaylistClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onPlaylistClickListener.onPlaylistClick(holder.itemView, pos, dataBean.getListid());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resultData.size();
    }

    public class OnlinePlaylistViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.playlist_avatar)
        ImageView playlistAvatar;
        @BindView(R.id.playlist_listener_count)
        TextView playlistListenerCount;
        @BindView(R.id.playlist_play_all)
        ImageView playlistPlayAll;
        @BindView(R.id.playlist_title)
        TextView playlistTitle;
        @BindView(R.id.playlist_subtitle)
        TextView playlistSubtitle;

        public OnlinePlaylistViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnPlaylistClickListener {
        void onPlaylistClick(View view, int position,String listId);
    }

    private OnPlaylistClickListener onPlaylistClickListener;

    public void setOnPlaylistClickListener(OnPlaylistClickListener onPlaylistClickListener) {
        this.onPlaylistClickListener = onPlaylistClickListener;
    }
}
