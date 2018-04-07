package com.fatwong.encore.ui.fragment.album;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.AlbumTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    @BindView(R.id.album_tab)
    TabLayout albumTab;
    @BindView(R.id.album_view_pager)
    ViewPager albumViewPager;
    @BindView(R.id.album_bottom)
    FrameLayout albumBottom;
    Unbinder unbinder;
    private AlbumTabAdapter mAlbumTabAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_album, container, false);
        unbinder = ButterKnife.bind(this, inflate);

        mAlbumTabAdapter = new AlbumTabAdapter(getChildFragmentManager());
        albumViewPager.setAdapter(mAlbumTabAdapter);
        albumViewPager.setCurrentItem(0);
        albumViewPager.setOffscreenPageLimit(mAlbumTabAdapter.getCount());
        albumTab.setupWithViewPager(albumViewPager);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
