package com.fatwong.encore.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fatwong.encore.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicDetailActivity extends AppCompatActivity {

    @BindView(R.id.background_album_art)
    ImageView backgroundAlbumArt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.avatar_layout)
    LinearLayout avatarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getEnterTransition().setDuration(500);
        }
    }
}
