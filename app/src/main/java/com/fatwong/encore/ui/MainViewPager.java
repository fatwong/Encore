package com.fatwong.encore.ui;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Isaac on 2018/2/27.
 */

public class MainViewPager extends ViewPager {
    public MainViewPager(Context context) {
        super(context);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    PointF mPointF = new PointF();
    OnSingleTouchListener mOnSingleTouchListener;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointF.x = ev.getX();
                mPointF.y = ev.getY();
                if(this.getChildCount() > 1) {
                    //通知父控件不进行拦截点击事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                mPointF.x = ev.getX();
                mPointF.y = ev.getY();
                if(this.getChildCount() > 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
            case MotionEvent.ACTION_UP:
                //判断是否为单纯的点击事件
                if (PointF.length(ev.getX() - mPointF.x, ev.getY() - mPointF.y) < (float) 5.0) {
                    onSingleTouch(this);
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void onSingleTouch(View view) {
        if (mOnSingleTouchListener != null) {
            mOnSingleTouchListener.onSingleTouch();
        }
    }

    public interface OnSingleTouchListener {
        void onSingleTouch();
    }

    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {
        this.mOnSingleTouchListener = onSingleTouchListener;
    }
}
