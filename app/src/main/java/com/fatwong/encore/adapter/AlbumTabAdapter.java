package com.fatwong.encore.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fatwong.encore.ui.fragment.album.NewSongFragment;

/**
 * Created by Isaac on 2018/3/6.
 */

public class AlbumTabAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[] {"歌单","MV","Hot Chart"};
    private NewSongFragment newSongFragment;

    public AlbumTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (newSongFragment == null) {
                    newSongFragment = newSongFragment.getInstance();
                }
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
