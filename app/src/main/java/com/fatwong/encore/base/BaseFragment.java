package com.fatwong.encore.base;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.fatwong.encore.ui.activity.PlayingActivity;
import com.fatwong.encore.service.MusicPlayerManager;

import kr.co.namee.permissiongen.PermissionGen;

/**
 * Created by Isaac on 2018/3/15.
 */

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Require Permission,for android 6.0+
        PermissionGen.needPermission(BaseFragment.this, 100,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                }
        );
    }

    public void showSnackBar(String toast) {
        Snackbar.make(getActivity().getWindow().getDecorView(), toast, Snackbar.LENGTH_SHORT).show();
    }

    public void showToast(int toastRes) {
        Toast.makeText(getActivity(), getString(toastRes), Toast.LENGTH_SHORT).show();
    }

    public boolean startPlayingActivity() {
        if (MusicPlayerManager.get().getCurrentSong() == null) {
            return false;
        }
        PlayingActivity.open(getActivity());
        return true;
    }

}
