package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.fatwong.encore.R;
import com.fatwong.encore.ui.fragment.DownloadCompleteFragment;
import com.fatwong.encore.ui.fragment.DownloadFragment;
import com.fatwong.encore.utils.CommonUtils;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadActivity extends AppCompatActivity implements XExecutor.OnAllTaskEndListener {

    @BindView(R.id.download_toolbar)
    Toolbar downloadToolbar;
    @BindView(R.id.download_tabs)
    TabLayout downloadTabs;
    @BindView(R.id.download_viewPager)
    ViewPager downloadViewPager;

    private OkDownload okDownload;

    public static void open(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, DownloadActivity.class);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        initOkDownload();
        initToolbar();
        initViewPager();
    }

    private void initOkDownload() {
        okDownload = OkDownload.getInstance();
        String path = Environment.getExternalStorageDirectory().getPath() + "/download/Encore";
        okDownload.setFolder(path);
        okDownload.getThreadPool().setCorePoolSize(5);
        okDownload.addOnAllTaskEndListener(this);
    }

    private void initViewPager() {
        if (downloadViewPager != null) {
            setupViewPager(downloadViewPager);
            downloadViewPager.setOffscreenPageLimit(2);
        }
        downloadTabs.setTabTextColors(0x7f0c014f, ThemeUtils.getThemeColorStateList(this, R.color.theme_color_primary).getDefaultColor());
        downloadTabs.setSelectedTabIndicatorColor(ThemeUtils.getThemeColorStateList(this, R.color.theme_color_primary).getDefaultColor());
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadTabs.setupWithViewPager(downloadViewPager);
            }
        }).start();
    }

    private void initToolbar() {
        downloadToolbar.setPadding(0x0, CommonUtils.getStatusHeight(this), 0x0, 0x0);
        setSupportActionBar(downloadToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.actionbar_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("\u4e0b\u8f7d\u7ba1\u7406");
        downloadToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        DownloadPagerAdapter adapter = new DownloadPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DownloadFragment(), "下载中");
        adapter.addFragment(new DownloadCompleteFragment(), "下载完成");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAllTaskEnd() {

    }

    class DownloadPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList();
        private final List<String> mFragmentTitles = new ArrayList();

        public DownloadPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public int getCount() {
            return mFragments.size();
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        okDownload.removeOnAllTaskEndListener(this);
    }
}
