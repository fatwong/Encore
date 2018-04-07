package com.fatwong.encore.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fatwong.encore.ui.fragment.local.LocalAlbumFragment;
import com.fatwong.encore.ui.fragment.local.LocalArtistFragment;
import com.fatwong.encore.ui.fragment.local.LocalMusicFragment;

/**
 * Created by Isaac on 2018/3/15.
 */

public class LocalMusicAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"单曲", "歌手", "专辑"};
    private LocalMusicFragment localMusicFragment;
    private LocalAlbumFragment localAlbumFragment;
    private LocalArtistFragment localArtistFragment;



    public LocalMusicAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (localMusicFragment == null) {
                    localMusicFragment = LocalMusicFragment.newInstance();
                }
                return localMusicFragment;
            case 1:
                if (localArtistFragment == null) {
                    localArtistFragment = LocalArtistFragment.newInstance();
                }
                return localArtistFragment;
            case 2:

                if (localAlbumFragment == null) {
                    localAlbumFragment = LocalAlbumFragment.newInstance();
                }
                return localAlbumFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
