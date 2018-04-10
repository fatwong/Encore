package com.fatwong.encore.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Album;
import com.fatwong.encore.bean.AlbumQuery;
import com.fatwong.encore.bean.LastfmAlbum;
import com.fatwong.encore.interfaces.AlbumInfoListener;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.net.LastfmClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalAlbumAdapter extends RecyclerView.Adapter<LocalAlbumAdapter.LocalAlbumViewHolder> {

    private List<Album> albumList;
    private Context mContext;
    private OnItemClickListener<Album> onAlbumClickListener;

    public OnItemClickListener<Album> getOnAlbumClickListener() {
        return onAlbumClickListener;
    }

    public void setOnAlbumClickListener(OnItemClickListener<Album> onAlbumClickListener) {
        this.onAlbumClickListener = onAlbumClickListener;
    }

    public LocalAlbumAdapter(List<Album> list, Context context) {
        albumList = list;
        mContext = context;
    }

    public void updateData(List<Album> list) {
        this.albumList = list;
    }

    @Override
    public LocalAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.local_album_listitem, parent, false);
        return new LocalAlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalAlbumViewHolder holder, int position) {
        Album model = albumList.get(position);
        holder.albumName.setText(model.album_name);
        holder.albumSongCount.setText(model.album_artist + "，" + model.number_of_songs + "首");
        LastfmClient.getInstance(mContext).getAlbumInfo(new AlbumQuery(model.album_name, model.album_artist), new AlbumInfoListener() {
            @Override
            public void albumInfoSuccess(LastfmAlbum album) {
                if (album != null && album.mArtwork != null) {
                    holder.albumImg.setImageURI(Uri.parse(album.mArtwork.get(2).mUrl));
                }
            }

            @Override
            public void albumInfoFailed() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList == null ? 0 : albumList.size();
    }

    public class LocalAlbumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.album_item)
        RelativeLayout albumItem;
        @BindView(R.id.album_img)
        SimpleDraweeView albumImg;
        @BindView(R.id.album_name)
        TextView albumName;
        @BindView(R.id.album_song_count)
        TextView albumSongCount;

        public LocalAlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            albumItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Album album = albumList.get(getAdapterPosition());
                    if (onAlbumClickListener != null) {
                        onAlbumClickListener.onItemClick(album, getAdapterPosition());
                    }
                }
            });
        }
    }
}
