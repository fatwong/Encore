package com.fatwong.encore.base;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fatwong.encore.R;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.service.MusicServiceHelper;
import com.fatwong.encore.ui.activity.PlayerActivity;
import com.fatwong.encore.ui.fragment.BottomFragment;

public class BaseActivity extends AppCompatActivity  {

    private String TAG = "BaseActivity";

    private BottomFragment bottomFragment;

    /**
     * @param outState 取消保存状态
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * @param savedInstanceState 取消保存状态
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //音乐服务的初始化
        MusicServiceHelper.get(getApplicationContext()).initService();
//        showBottomControl(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        unbindService();
    }

    public void unbindService() {

    }

    public void changeActionbarSkinMode(ActionBar mActionbar, boolean isNight){
        int actionbarColor;
        if(isNight) {
            actionbarColor= R.color.actionbar_night;
        }else{
            actionbarColor=R.color.actionbar_day;
        }
        setBackgroundAlpha(mActionbar, getResources().getColor(actionbarColor));
    }

    public void setBackgroundAlpha(ActionBar view, int baseColor) {
        int rgb = baseColor;
        Drawable drawable = new ColorDrawable(rgb);
        if(view!=null)
            view.setBackgroundDrawable(drawable);
    }

    public boolean startPlayingActivity() {
        if (MusicPlayerManager.get().getCurrentSong() == null) {
            return false;
        }
        PlayerActivity.open(this);
        return true;
    }

}

