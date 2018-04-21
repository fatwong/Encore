package com.fatwong.encore.ui.fragment;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.fatwong.encore.R;
import com.fatwong.encore.service.MusicPlayerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TimingFragment extends DialogFragment {


    @BindView(R.id.timing_10min)
    TextView timing10min;
    @BindView(R.id.timing_20min)
    TextView timing20min;
    @BindView(R.id.timing_30min)
    TextView timing30min;
    @BindView(R.id.timing_45min)
    TextView timing45min;
    @BindView(R.id.timing_60min)
    TextView timing60min;
    @BindView(R.id.timing_90min)
    TextView timing90min;
    Unbinder unbinder;

    public TimingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_timing, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.timing_10min, R.id.timing_20min, R.id.timing_30min, R.id.timing_45min, R.id.timing_60min, R.id.timing_90min})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.timing_10min:
                setTime(10 * 60 * 1000);
                Toast.makeText(getContext(), "将在10分钟后停止播放", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.timing_20min:
                setTime(20 * 60 * 1000);
                Toast.makeText(getContext(), "将在20分钟后停止播放", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.timing_30min:
                setTime(30 * 60 * 1000);
                Toast.makeText(getContext(), "将在30分钟后停止播放", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.timing_45min:
                setTime(45 * 60 * 1000);
                Toast.makeText(getContext(), "将在45分钟后停止播放", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.timing_60min:
                setTime(60 * 60 * 1000);
                Toast.makeText(getContext(), "将在60分钟后停止播放", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.timing_90min:
                setTime(90 * 60 * 1000);
                Toast.makeText(getContext(), "将在90分钟后停止播放", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }

    private void setTime(long time){
        //Android自带的倒计时的类
        CountDownTimer timer = new CountDownTimer(time, 1000) {// 第一个参数是总共时间，第二个参数是间隔触发时间
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (MusicPlayerManager.get().isPlaying()) {
                    MusicPlayerManager.get().pause();
                }
                getActivity().finish();
            }
        };
        timer.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置fragment高度 、宽度
        int dialogHeight = (int) (getActivity().getResources().getDisplayMetrics().heightPixels * 0.71);
        int dialogWidth = (int) (getActivity().getResources().getDisplayMetrics().widthPixels * 0.79);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true);

    }

}
