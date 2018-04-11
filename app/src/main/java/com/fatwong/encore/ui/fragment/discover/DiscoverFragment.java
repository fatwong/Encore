package com.fatwong.encore.ui.fragment.discover;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.DiscoverTabAdapter;
import com.fatwong.encore.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscoverFragment extends BaseFragment {

    @BindView(R.id.discover_tab)
    TabLayout albumTab;
    @BindView(R.id.discover_view_pager)
    ViewPager albumViewPager;
    @BindView(R.id.album_bottom)
    FrameLayout albumBottom;
    Unbinder unbinder;
    private DiscoverTabAdapter mDiscoverTabAdapter;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_discover, container, false);
        unbinder = ButterKnife.bind(this, inflate);

        mDiscoverTabAdapter = new DiscoverTabAdapter(getChildFragmentManager());
        albumViewPager.setAdapter(mDiscoverTabAdapter);
        albumViewPager.setCurrentItem(0);
        albumViewPager.setOffscreenPageLimit(mDiscoverTabAdapter.getCount());
        albumTab.setupWithViewPager(albumViewPager);
        return inflate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
