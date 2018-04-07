package com.fatwong.encore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bilibili.magicasakura.widgets.TintToolbar;
import com.fatwong.encore.R;
import com.fatwong.encore.bean.Playlist;
import com.fatwong.encore.db.PlaylistManager;
import com.fatwong.encore.event.UpdatePlaylistEvent;
import com.fatwong.encore.utils.PhotoUtils;
import com.fatwong.encore.utils.RxBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePlaylistActivity extends AppCompatActivity {

    @BindView(R.id.create_playlist_toolbar)
    TintToolbar createPlaylistToolbar;
    @BindView(R.id.playlist_cover)
    ImageView playlistCover;
    @BindView(R.id.playlist_title)
    TextView playlistTitle;
    @BindView(R.id.playlist_desc)
    EditText playlistDesc;
    private int cid;
    private Playlist currentPlaylist;
    private boolean playlistHasChanged;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;

    public static void open(Context context) {
        open(context, -1);
    }

    public static void open(Context context, int cid) {
        Intent intent = new Intent(context, CreatePlaylistActivity.class);
        intent.putExtra("cid", cid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        ButterKnife.bind(this);
        setSupportActionBar(createPlaylistToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            cid = getIntent().getIntExtra("cid", -1);
            if (cid == -1) {
                getSupportActionBar().setTitle(R.string.collection_create_title);
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);
        initData();
    }
    private void initData() {
        playlistHasChanged = false;
        if (cid != -1) {
            currentPlaylist = PlaylistManager.getInstance().getPlaylistById(cid);
            playlistCover.setImageURI(Uri.parse(currentPlaylist.getCoverUrl()));
            playlistTitle.setText(currentPlaylist.getTitle());
            playlistDesc.setText(currentPlaylist.getDescription());
        } else {
            currentPlaylist = new Playlist(-1, getString(R.string.collection_title_default), "", 0, "");
        }
        playlistDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                playlistHasChanged = true;
                String desc = editable.toString();
                currentPlaylist.setDescription(desc);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_store) {
            if (!playlistHasChanged) {
                Toast.makeText(CreatePlaylistActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            PlaylistManager.getInstance().setPlaylist(currentPlaylist);
            playlistHasChanged = false;
            RxBus.getInstance().post(new UpdatePlaylistEvent(true));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (playlistHasChanged) {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.collection_dialog_update_title)
                    .content(R.string.collection_dialog_update_content)
                    .positiveText(R.string.confirm)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            PlaylistManager.getInstance().setPlaylist(currentPlaylist);
                            playlistHasChanged = false;
                            RxBus.getInstance().post(new UpdatePlaylistEvent(true));
                            CreatePlaylistActivity.this.onBackPressed();
                            dialog.dismiss();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            playlistHasChanged = false;
                            CreatePlaylistActivity.this.onBackPressed();
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int output_X = 480, output_Y = 480;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.TAKE_PHOTO://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, output_X, output_Y, PhotoUtils.CROP_PICTURE);
                    break;
                case PhotoUtils.CHOOSE_PICTURE://访问相册完成回调
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(this, "com.fatwong.encore.fileprovider", new File(newUri.getPath()));
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, output_X, output_Y, PhotoUtils.CROP_PICTURE);

                    break;
                case PhotoUtils.CROP_PICTURE:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    Drawable drawable = new BitmapDrawable(bitmap);
                    playlistHasChanged = true;
                    playlistCover.setImageBitmap(bitmap);
                    currentPlaylist.setCoverUrl(cropImageUri.getPath());
                    break;
            }
        }
    }

    @OnClick({R.id.create_playlist_toolbar, R.id.playlist_cover, R.id.playlist_title, R.id.playlist_desc})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.create_playlist_toolbar:
                break;
            case R.id.playlist_cover:
                new MaterialDialog.Builder(this)
                        .title(R.string.collection_dialog_cover_title)
                        .items(R.array.collection_cover)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position) {
                                    case 0:
                                        imageUri = Uri.fromFile(fileUri);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                            //通过FileProvider创建一个content类型的Uri
                                            imageUri = FileProvider.getUriForFile(CreatePlaylistActivity.this, "com.fatwong.encore.fileprovider", fileUri);
                                        PhotoUtils.takePicture(CreatePlaylistActivity.this, imageUri, PhotoUtils.TAKE_PHOTO);
                                        break;
                                    case 1:
                                        PhotoUtils.openPic(CreatePlaylistActivity.this, PhotoUtils.CHOOSE_PICTURE);
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.playlist_title:
                new MaterialDialog.Builder(this)
                        .title(R.string.collection_dialog_name)
                        .inputRangeRes(2, 20, R.color.theme_color_PrimaryAccent)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(currentPlaylist.getTitle(), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (!TextUtils.isEmpty(input)) {
                                    currentPlaylist.setTitle(String.valueOf(input));
                                    playlistTitle.setText(input);
                                    playlistHasChanged = true;
                                }
                            }
                        })
                        .negativeText(R.string.cancel)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                playlistHasChanged = false;
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.playlist_desc:
                break;
        }
    }
}
