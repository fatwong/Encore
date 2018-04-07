package com.fatwong.encore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fatwong.encore.MyApplication;
import com.fatwong.encore.db.DatabaseHelper;

public class BaseDao {

    protected SQLiteDatabase database;
    protected DatabaseHelper databaseHelper;

    public BaseDao() {
        databaseHelper = new DatabaseHelper(MyApplication.getInstance().getApplicationContext());
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        database.close();
        databaseHelper.close();
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        Cursor c = database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return c;
    }

    public long insert(String table, String nullColumnHack, ContentValues values){
        return database.insert(table, nullColumnHack, values);
    }


    public int delete(String table, String whereClause, String[] whereArgs){
        return database.delete(table, whereClause, whereArgs);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        return database.update(table, values, whereClause, whereArgs);
    }

    public long replace(String table, String nullColumnHack, ContentValues initialValues){
        return database.replace(table, nullColumnHack, initialValues);
    }
}
