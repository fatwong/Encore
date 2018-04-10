package com.fatwong.encore.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintImageView;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.OverFlowItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PopupAdapter extends RecyclerView.Adapter<PopupAdapter.ListItemViewHolder> implements View.OnClickListener {

    private List<OverFlowItem> overFlowItemList;
    private Activity mContext;
    private OnRecyclerViewItemClickListener onItemClickListener;

    public PopupAdapter(Activity context, List<OverFlowItem> list) {
        overFlowItemList = list;
        mContext = context;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_listitem, parent, false);
        view.setOnClickListener(this);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        OverFlowItem selectedItem = overFlowItemList.get(position);
        holder.popupItemImg.setImageResource(selectedItem.getAvatar());
        holder.popupItemImg.setImageTintList(R.color.theme_color_primary);
        holder.popItemTitle.setText(selectedItem.getTitle());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return overFlowItemList.size();
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    //定义接口
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.popup_item_img)
        TintImageView popupItemImg;
        @BindView(R.id.pop_item_title)
        TextView popItemTitle;

        public ListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
