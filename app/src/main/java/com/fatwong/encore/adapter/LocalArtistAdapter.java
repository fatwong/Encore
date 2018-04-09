package com.fatwong.encore.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Artist;
import com.fatwong.encore.bean.ArtistQuery;
import com.fatwong.encore.bean.LastfmArtist;
import com.fatwong.encore.interfaces.ArtistInfoListener;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.net.LastfmClient;
import com.fatwong.encore.service.MusicPlayerManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalArtistAdapter extends RecyclerView.Adapter<LocalArtistAdapter.LocalArtistViewHolder> {

    private List<Artist> artistList;
    private Context mContext;
    private OnItemClickListener onArtistClickListener;

    public OnItemClickListener<Artist> getOnArtistClickListener() {
        return onArtistClickListener;
    }

    public void setOnArtistClickListener(OnItemClickListener<Artist> onArtistClickListener) {
        this.onArtistClickListener = onArtistClickListener;
    }

    public LocalArtistAdapter(List<Artist> list, Context context) {
        this.artistList = list;
        this.mContext = context;
    }

    public void updateData(List<Artist> list) {
        this.artistList = list;
    }

    @Override
    public LocalArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.local_artist_listitem, parent, false);
        return new LocalArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalArtistViewHolder holder, int position) {
        Artist model = artistList.get(position);
        holder.artistName.setText(model.artist_name);
        holder.artistSongCount.setText(model.number_of_tracks + "首");
        if (MusicPlayerManager.get().isPlaying()) {
            if (MusicPlayerManager.get().getCurrentSong().getArtistId() == (model.artist_id)) {
                holder.artistMoreButton.setImageResource(R.drawable.song_play_icon);
                holder.artistMoreButton.setImageTintList(R.color.theme_color_primary);
            } else {
                holder.artistMoreButton.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
            }
        }
        LastfmClient.getInstance(mContext).getArtistInfo(new ArtistQuery(model.artist_name), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(LastfmArtist artist) {
                if (artist != null && artist.mArtwork != null) {
                    holder.artistImg.setImageURI(Uri.parse(artist.mArtwork.get(2).mUrl));
                }
            }

            @Override
            public void artistInfoFailed() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList == null ? 0 : artistList.size();
    }

    public class LocalArtistViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.artist_more_button)
        TintImageView artistMoreButton;
        @BindView(R.id.artist_img)
        SimpleDraweeView artistImg;
        @BindView(R.id.artist_name)
        TextView artistName;
        @BindView(R.id.artist_song_count)
        TextView artistSongCount;
        @BindView(R.id.artist_item)
        RelativeLayout artistItem;

        public LocalArtistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            artistItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Artist artist = artistList.get(getAdapterPosition());
                    if (onArtistClickListener != null) {
                        onArtistClickListener.onItemClick(artist, getAdapterPosition());
                    }
                }
            });
        }
    }
}
