package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.Dynamic;
import com.fatwong.encore.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.VideoRecyclerViewHolder> {

    private Context mContext;
    public static final String TAG = "RecyclerViewVideoAdapter";
    private List<Dynamic.BodyBean.DetailBean> result = new ArrayList<>();

    public VideoRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public VideoRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_listitem, parent, false);
        return new VideoRecyclerViewHolder(view);
    }

    public void setData(List<Dynamic.BodyBean.DetailBean> data) {
        this.result = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(VideoRecyclerViewHolder holder, int position) {
        Dynamic.BodyBean.DetailBean resultBean = result.get(position);
        holder.videoPlayer.setUp(resultBean.getMp4Url(), JZVideoPlayer.SCREEN_WINDOW_LIST);
        holder.videoPlayer.thumbImageView.getAdjustViewBounds();
        holder.videoPlayer.backButton.setVisibility(View.GONE);
        holder.videoPlayer.thumbImageView.setImageResource(R.drawable.a8p);
        ImageUtils.glideWith(holder.videoPlayer.getContext(), resultBean.getUrl(), R.drawable.a8p, holder.videoPlayer.thumbImageView);
        ImageUtils.glideWithRoundImage(holder.videoPlayer.getContext(), resultBean.getPhoto(), holder.videoUploaderAvatar);
        holder.videoUploader.setText(resultBean.getNickname());
        holder.videoDesc.setText(resultBean.getTitle());
        holder.videoUpdateTime.setText(resultBean.getCreatetime());

    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public class VideoRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_uploader_avatar)
        ImageView videoUploaderAvatar;
        @BindView(R.id.video_uploader)
        TextView videoUploader;
        @BindView(R.id.video_update_time)
        TextView videoUpdateTime;
        @BindView(R.id.video_desc)
        TextView videoDesc;
        @BindView(R.id.video_player)
        JZVideoPlayerStandard videoPlayer;

        public VideoRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
