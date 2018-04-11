package com.fatwong.encore.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fatwong.encore.ui.fragment.MVFragment;
import com.fatwong.encore.ui.fragment.discover.ChartFragment;
import com.fatwong.encore.ui.fragment.discover.OnlinePlaylistFragment;

/**
 * Created by Isaac on 2018/3/6.
 */

public class DiscoverTabAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[] {"歌单","MV","Hot Chart"};
    private OnlinePlaylistFragment onlinePlaylistFragment;
    private MVFragment mvFragment;
    private ChartFragment chartFragment;

    public DiscoverTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (onlinePlaylistFragment == null) {
                    onlinePlaylistFragment = OnlinePlaylistFragment.newInstance();
                }
                return onlinePlaylistFragment;
            case 1:
                if (mvFragment == null) {
                    mvFragment = MVFragment.newInstance();
                }
                return mvFragment;
            case 2:
                if (chartFragment == null) {
                    chartFragment = ChartFragment.newInstance();
                }
                return chartFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
