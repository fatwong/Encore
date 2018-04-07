package com.fatwong.encore.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 文件下载， 上传， 获取， 写入
 * Created by Isaac on 2018/3/6.
 */

public class FileUtils {
    private static Context mContext;
    private static final int DELAY_TIME = 10000;

    public final static String CACHE_DIR = "CNMusic";
    public final static String GLIDE_CACHE_DIR = "glide";
    public final static String SONG_CACHE_DIR = "songs";

    public final static String APK_NAME = "cnmusic.apk";

    /**
     * 应用关联的图片存储路径
     */
    public static String getAppPictureDir(Context context) {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return file.getPath();
    }

    public static boolean existFile(String path) {
        File path1 = new File(path);
        return path1.exists();
    }

}
