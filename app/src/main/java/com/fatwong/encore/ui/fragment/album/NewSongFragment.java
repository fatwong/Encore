package com.fatwong.encore.ui.fragment.album;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fatwong.encore.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewSongFragment extends Fragment {


    public NewSongFragment() {
        // Required empty public constructor
    }

    public NewSongFragment getInstance() {
        NewSongFragment newSongFragment = new NewSongFragment();
        return newSongFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_music, container, false);
    }

}
