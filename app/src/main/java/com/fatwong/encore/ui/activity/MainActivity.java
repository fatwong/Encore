package com.fatwong.encore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.MyApplication;
import com.fatwong.encore.R;
import com.fatwong.encore.adapter.MainNavAdapter;
import com.fatwong.encore.base.BaseActivity;
import com.fatwong.encore.bean.Song;
import com.fatwong.encore.service.MusicPlayerManager;
import com.fatwong.encore.ui.MainViewPager;
import com.fatwong.encore.ui.fragment.DynamicFragment;
import com.fatwong.encore.ui.fragment.NetSearchFragment;
import com.fatwong.encore.ui.fragment.discover.DiscoverFragment;
import com.fatwong.encore.ui.fragment.local.LocalFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.bar_net)
    ImageView barNet;
    @BindView(R.id.bar_music)
    ImageView barMusic;
    @BindView(R.id.bar_search)
    ImageView barFriends;
    @BindView(R.id.bar_player)
    ImageView barPlayer;
    @BindView(R.id.toolbar)
    TintToolbar toolbar;
    @BindView(R.id.main_viewpager)
    MainViewPager mainViewPager;
    @BindView(R.id.drawer_main)
    DrawerLayout drawerMain;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    @BindView(R.id.main_nav_menu)
    ListView mainNavMenu;

    private ActionBar mActionBar;
    private ArrayList<ImageView> tabs = new ArrayList<>();
    private MenuItem menuItem;
    private long time;
    private boolean isNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //夜间模式
        if (MyApplication.appConfig.getNightModeSwitch()) {
            this.setTheme(R.style.Theme_setting_night);
            isNight = true;
        } else {
            this.setTheme(R.style.Theme_setting_day);
            isNight = false;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolBar();
        bottomNavigation.setSelectedItemId(R.id.item_my_music);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_discover:
                        mainViewPager.setCurrentItem(0);
                        break;
                    case R.id.item_my_music:
                        mainViewPager.setCurrentItem(1);
                        break;
                    case R.id.item_settings:
                        mainViewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });
        setCustomViewPager();
        setUpDrawer();
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        mActionBar.setTitle("");
    }

    private void setCustomViewPager() {
        tabs.add(barNet);
        tabs.add(barMusic);
        tabs.add(barFriends);

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        LocalFragment localFragment = new LocalFragment();
        DiscoverFragment discoverFragment = new DiscoverFragment();
        NetSearchFragment netSearchFragment = new NetSearchFragment();

        myPagerAdapter.addFragment(discoverFragment);
        myPagerAdapter.addFragment(localFragment);
        myPagerAdapter.addFragment(netSearchFragment);
        mainViewPager.setAdapter(myPagerAdapter);
        mainViewPager.setCurrentItem(1);
        barMusic.setSelected(true);

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /***
     * 初始化侧滑菜单
     */
    private void setUpDrawer() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mainNavMenu.addHeaderView(inflater.inflate(R.layout.nav_header_main, mainNavMenu, false));
        mainNavMenu.setAdapter(new MainNavAdapter(this));
        mainNavMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        //当前的模式
                        boolean isNightMode = MyApplication.appConfig.getNightModeSwitch();
                        MyApplication.appConfig.setNightModeSwitch(!isNightMode);
                        changeActionbarSkinMode(getSupportActionBar(), !isNightMode);
                        drawerMain.closeDrawers();
                        break;
                    case 2:
//                        //定时关闭音乐
//                        TimingFragment timingFragment = new TimingFragment();
//                        timingFragment.show(getSupportFragmentManager(), "timing");
//                        drawerMain.closeDrawers();
//                        break;
                    case 3:
                        //退出
                        if (MusicPlayerManager.get().isPlaying()) {
                            MusicPlayerManager.get().pause();
                        }
                        unbindService();
                        finish();
                        break;

                }
            }
        });
    }

    private void changeTabs(int position) {
        for (int i = 0; i < tabs.size(); i++) {
            if (position == i) {
                tabs.get(i).setSelected(true);
            } else {
                tabs.get(i).setSelected(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerMain.openDrawer(Gravity.LEFT);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.bar_net, R.id.bar_music, R.id.bar_search, R.id.bar_player})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bar_net:
                mainViewPager.setCurrentItem(0);
                break;
            case R.id.bar_music:
                mainViewPager.setCurrentItem(1);
                break;
            case R.id.bar_search:
                mainViewPager.setCurrentItem(2);
                break;
            case R.id.bar_player:
                Song song = MusicPlayerManager.get().getCurrentSong();
                if (song == null) {
                    if (song == null) {
                        Toast.makeText(this, "当前无歌曲播放", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                startPlayingActivity();
                break;
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time > 1000) {
                Toast.makeText(this, "Click twice to close the app", Toast.LENGTH_SHORT);
                time = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
