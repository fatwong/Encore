package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fatwong.encore.R;
import com.fatwong.encore.bean.MVList;
import com.fatwong.encore.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MVRecyclerAdapter extends RecyclerView.Adapter<MVRecyclerAdapter.MVRecyclerViewHolder> {

    private Context mContext;
    private List<MVList.ResultBean.MvListBean> result = new ArrayList<>();
    private OnItemClickListener onMVClickListener;

    public MVRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public MVRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mv_listitem, parent, false);
        return new MVRecyclerViewHolder(view);
    }
    public void setData(List<MVList.ResultBean.MvListBean> data) {
        this.result = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MVRecyclerViewHolder holder, int position) {
        final MVList.ResultBean.MvListBean resultBean = result.get(position);
        ImageUtils.glideWith(mContext, resultBean.getThumbnail2(), R.drawable.a8p, holder.mvThumbnail);
        if (!TextUtils.isEmpty(resultBean.getTitle())) {
            holder.mvTitle.setText(resultBean.getTitle());
        } else {
            holder.mvTitle.setText(resultBean.getSubtitle());
        }
        holder.mvArtist.setText(resultBean.getArtist());
        if (onMVClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onMVClickListener.onItemClick(holder.itemView, pos, resultBean.getMv_id(), resultBean.getTitle());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public class MVRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mv_thumbnail)
        ImageView mvThumbnail;
        @BindView(R.id.mv_title)
        TextView mvTitle;
        @BindView(R.id.mv_artist)
        TextView mvArtist;

        public MVRecyclerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position,String mvID,String mvName);
    }

    public OnItemClickListener getOnMVClickListener() {
        return onMVClickListener;
    }

    public void setOnMVClickListener(OnItemClickListener onMVClickListener) {
        this.onMVClickListener = onMVClickListener;
    }

}
