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
import com.fatwong.encore.bean.Chart;
import com.fatwong.encore.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartRecyclerAdapter extends RecyclerView.Adapter<ChartRecyclerAdapter.ChartRecyclerViewHolder> {

    private Context mContext;
    public static final String TAG = "RecyclerViewVideoAdapter";
    private List<Chart.ContentBean> result = new ArrayList<>();

    public ChartRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public ChartRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChartRecyclerViewHolder holder = new ChartRecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chart_listitem, parent,false));
        return holder;
    }

    public void setData(List<Chart.ContentBean> data) {
        this.result = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ChartRecyclerViewHolder holder, int position) {
        //排行榜的实例
        final Chart.ContentBean contentBean = result.get(position);
        holder.chartTitle.setText(contentBean.getName());
        //图片下载
        ImageUtils.glideWith(mContext, contentBean.getPic_s192(), R.drawable.a8y, holder.chartIconImg);
        //top3
        holder.chartItemTop1Song.setText(result.get(position).getContent().get(0).getTitle() + "-" + result.get(position).getContent().get(0).getAuthor());
        holder.chartItemTop2Song.setText(result.get(position).getContent().get(1).getTitle() + "-" + result.get(position).getContent().get(1).getAuthor());
        holder.chartItemTop3Song.setText(result.get(position).getContent().get(2).getTitle() + "-" + result.get(position).getContent().get(2).getAuthor());

        // 如果设置了回调，则设置点击事件
        if (onChartClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int type = contentBean.getType();
                    onChartClickListener.onItemClick(holder.itemView, type);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public interface OnChartClickListener {
        void onItemClick(View view, int type);
    }

    private OnChartClickListener onChartClickListener;

    public void setOnChartClickListener(OnChartClickListener onChartClickListener) {
        this.onChartClickListener = onChartClickListener;
    }

    static class ChartRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chart_icon_img)
        ImageView chartIconImg;
        @BindView(R.id.chart_icon_play_all)
        ImageView chartIconPlayAll;
        @BindView(R.id.chart_icon_layout)
        RelativeLayout chartIconLayout;
        @BindView(R.id.chart_enter_button)
        ImageView chartEnterButton;
        @BindView(R.id.chart_title)
        TextView chartTitle;
        @BindView(R.id.chart_item_top1_song)
        TextView chartItemTop1Song;
        @BindView(R.id.chart_item_top2_song)
        TextView chartItemTop2Song;
        @BindView(R.id.chart_item_top3_song)
        TextView chartItemTop3Song;
        @BindView(R.id.chart_item_layout)
        RelativeLayout chartItemLayout;

        public ChartRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
