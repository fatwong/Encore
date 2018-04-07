package com.fatwong.encore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fatwong.encore.db.dao.PlaylistDao;
import com.fatwong.encore.db.dao.PlaylistRelationDao;
import com.fatwong.encore.db.dao.SongDao;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "encore.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SongDao.createTable());
        sqLiteDatabase.execSQL(SongDao.createIndex());
        //收藏夹表
        sqLiteDatabase.execSQL(PlaylistDao.createTable());
        //收藏夹关联表
        sqLiteDatabase.execSQL(PlaylistRelationDao.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
