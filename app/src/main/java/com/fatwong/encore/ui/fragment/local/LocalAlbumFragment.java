package com.fatwong.encore.ui.fragment.local;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;
import com.fatwong.encore.adapter.LocalAlbumAdapter;
import com.fatwong.encore.bean.AlbumInfo;
import com.fatwong.encore.interfaces.OnItemClickListener;
import com.fatwong.encore.ui.activity.LocalAlbumDetailActivity;
import com.fatwong.encore.utils.MusicUtils;

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
    private LocalAlbumAdapter localAlbumAdapter;

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
        localAlbumAdapter = new LocalAlbumAdapter(null, getActivity());
        localAlbumRecycler.setAdapter(localAlbumAdapter);
        localAlbumRecycler.setHasFixedSize(true);
        ((SimpleItemAnimator)localAlbumRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        setItemDecoration();
        reloadAdapter();
        localAlbumAdapter.setOnAlbumClickListener(new OnItemClickListener<AlbumInfo>() {
            @Override
            public void onItemClick(AlbumInfo item, int position) {
                LocalAlbumDetailActivity.open(getContext(), item);
            }

            @Override
            public void onItemSettingClick(View view, AlbumInfo item, int position) {

            }
        });
        return view;
    }

    public void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<AlbumInfo> albumInfoList = MusicUtils.queryAlbums(getActivity());
                if (albumInfoList != null) {
                    localAlbumAdapter.updateData(albumInfoList);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                localAlbumAdapter.notifyDataSetChanged();
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
        localAlbumAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
