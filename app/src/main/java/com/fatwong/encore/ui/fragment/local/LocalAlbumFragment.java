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
import com.fatwong.encore.adapter.LocalAlbumRecyclerAdapter;
import com.fatwong.encore.bean.Album;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.ui.activity.LocalAlbumDetailActivity;
import com.fatwong.encore.utils.LocalMusicLibrary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocalAlbumFragment extends Fragment {


    @BindView(R.id.local_album_recycler)
    RecyclerView localAlbumRecycler;
    Unbinder unbinder;

    private LinearLayoutManager layoutManager;
    private LocalAlbumRecyclerAdapter localAlbumRecyclerAdapter;

    public LocalAlbumFragment() {
        // Required empty public constructor
    }

    public static LocalAlbumFragment newInstance() {
        LocalAlbumFragment localAlbumFragment = new LocalAlbumFragment();
        return localAlbumFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_album, container, false);
        unbinder = ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(getActivity());
        localAlbumRecycler.setLayoutManager(layoutManager);
        localAlbumRecyclerAdapter = new LocalAlbumRecyclerAdapter(null, getActivity());
        localAlbumRecycler.setAdapter(localAlbumRecyclerAdapter);
        localAlbumRecycler.setHasFixedSize(true);
        ((SimpleItemAnimator)localAlbumRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        setItemDecoration();
        reloadAdapter();
        localAlbumRecyclerAdapter.setOnAlbumClickListener(new OnItemClickListener<Album>() {
            @Override
            public void onItemClick(Album item, int position) {
                LocalAlbumDetailActivity.open(getContext(), item);
            }

            @Override
            public void onItemSettingClick(View view, Album item, int position) {

            }
        });
        return view;
    }

    public void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Album> albumList = LocalMusicLibrary.getAllAlbums(getActivity());
                if (albumList != null) {
                    localAlbumRecyclerAdapter.updateData(albumList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                localAlbumRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private void setItemDecoration() {
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        localAlbumRecycler.addItemDecoration(itemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();
        localAlbumRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
