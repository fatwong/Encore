package com.fatwong.encore.interfaces;

import android.view.View;

/**
 * Created by Isaac on 2018/3/9.
 */

public interface OnItemClickListener<T> {
    void onItemClick(T item, int position);
    void onItemSettingClick(View view, T item, int position);
}
