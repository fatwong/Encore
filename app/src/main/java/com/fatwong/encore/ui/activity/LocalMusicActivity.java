package com.fatwong.encore.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.R;
import com.fatwong.encore.base.BaseActivity;
import com.fatwong.encore.adapter.LocalMusicTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocalMusicActivity extends BaseActivity {

    @BindView(R.id.local_music_toolbar)
    TintToolbar localMusicToolbar;
    @BindView(R.id.local_music_tab)
    TabLayout localMusicTab;
    @BindView(R.id.local_music_viewpager)
    ViewPager localMusicViewPager;
    private ActionBar actionBar;
    private LocalMusicTabAdapter localMusicTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        ButterKnife.bind(this);
        setToolBar();
        localMusicTabAdapter = new LocalMusicTabAdapter(getSupportFragmentManager());
        localMusicViewPager.setAdapter(localMusicTabAdapter);
        localMusicViewPager.setCurrentItem(0);
        localMusicViewPager.setOffscreenPageLimit(localMusicTabAdapter.getCount());
        localMusicTab.setupWithViewPager(localMusicViewPager);

    }

    private void setToolBar() {
        setSupportActionBar(localMusicToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setTitle("本地音乐");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
