package com.fatwong.encore.ui.fragment.local;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.LocalArtistAdapter;
import com.fatwong.encore.bean.Artist;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.ui.activity.LocalArtistDetailActivity;
import com.fatwong.encore.utils.LocalMusicLibrary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalArtistFragment extends Fragment {


    @BindView(R.id.local_artist_recycler)
    RecyclerView localArtistRecycler;
    Unbinder unbinder;

    private LinearLayoutManager layoutManager;
    private LocalArtistAdapter localArtistAdapter;

    public LocalArtistFragment() {
        // Required empty public constructor
    }

    public static LocalArtistFragment newInstance() {
        LocalArtistFragment localArtistFragment = new LocalArtistFragment();
        return localArtistFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_artist, container, false);
        unbinder = ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(getActivity());
        localArtistRecycler.setLayoutManager(layoutManager);
        localArtistAdapter = new LocalArtistAdapter(null, getActivity());
        localArtistRecycler.setAdapter(localArtistAdapter);
        localArtistRecycler.setHasFixedSize(true);
        ((SimpleItemAnimator)localArtistRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        setItemDecoration();
        reloadAdapter();
        localArtistAdapter.setOnArtistClickListener(new OnItemClickListener<Artist>() {
            @Override
            public void onItemClick(Artist item, int position) {
                LocalArtistDetailActivity.open(getContext(), item);
            }

            @Override
            public void onItemSettingClick(View view, Artist item, int position) {

            }
        });
        return view;
    }

    public void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Artist> artistList = LocalMusicLibrary.getAllArtists(getActivity());
                if (artistList != null) {
                    localArtistAdapter.updateData(artistList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                localArtistAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void setItemDecoration() {
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        localArtistRecycler.addItemDecoration(itemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();
        localArtistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
