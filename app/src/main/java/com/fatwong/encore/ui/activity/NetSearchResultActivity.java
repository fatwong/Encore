package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.SearchSongInfo;
import com.fatwong.encore.interfaces.APIService;
import com.fatwong.encore.ui.fragment.NetSearchSongFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NetSearchResultActivity extends AppCompatActivity {

    @BindView(R.id.search_result_toolbar)
    TintToolbar searchResultToolbar;
    @BindView(R.id.search_result_tab)
    TabLayout searchResultTab;
    @BindView(R.id.search_result_viewpager)
    ViewPager searchResultViewpager;
    private ActionBar actionBar;
    private NetSearchResultTabAdapter adapter;
    private static String queryString;
    ArrayList<SearchSongInfo> songResults = new ArrayList<>();
    private NetSearchResultActivity mContext;

    public static void open(Context context, String query) {
        queryString = query;
        Intent intent = new Intent();
        intent.setClass(context, NetSearchResultActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_search_result);
        ButterKnife.bind(this);
        setToolBar();
        search(queryString);
    }

    private void search(final String key) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Gson gson = new Gson();
                    okhttp3.Response response = OkGo.get(APIService.SEARCH_URL + key).execute();
                    String data = response.body().string();
                    JsonArray songArray = new JsonParser().parse(data).getAsJsonObject().get("song_list").getAsJsonArray();
                    for (JsonElement song : songArray) {
                        SearchSongInfo songInfo = gson.fromJson(song, SearchSongInfo.class);
                        songResults.add(songInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter = new NetSearchResultTabAdapter(getSupportFragmentManager());
                adapter.addFragment(NetSearchSongFragment.newInstance(songResults), "单曲");
                searchResultViewpager.setAdapter(adapter);
                searchResultViewpager.setOffscreenPageLimit(adapter.getCount());
                searchResultTab.setupWithViewPager(searchResultViewpager);
            }
        }.execute();
    }

    private void setToolBar() {
        setSupportActionBar(searchResultToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setTitle("搜索结果");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class NetSearchResultTabAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public NetSearchResultTabAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
