package com.fatwong.encore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.fatwong.encore.bean.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDao extends BaseDao {

    private static final String TABLE_NAME = "PLAYLIST";

    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_COVER_URL = "cover_url";
    private final static String COLUMN_DESCRIPTION = "description";
    private final static String COLUMN_COUNT = "count";

    /**
     * 建表sql
     */
    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append(COLUMN_TITLE + " varchar(100),");
        sb.append(COLUMN_COVER_URL + " varchar(200),");
        sb.append(COLUMN_DESCRIPTION + " TEXT,");
        sb.append(COLUMN_COUNT + " INTEGER");
        sb.append(");");
        return sb.toString();
    }

    /**
     * 获取表上的所有歌单
     */
    public List<Playlist> queryAll() {
        List<Playlist> playlists = new ArrayList<>();
        Cursor cursor = query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            playlists.add(getPlaylist(cursor));
        }
        cursor.close();
        return playlists;
    }

    /**
     * 获取表中的某个歌单
     */
    public Playlist query(int id) {
        Playlist playlist = null;
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        List<Playlist> collectionList = new ArrayList<>();
        Cursor cursor = query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            playlist = getPlaylist(cursor);
        }
        cursor.close();
        return playlist;
    }


    /**
     * 插入一条收藏夹记录
     *
     * @param playlist
     */
    public long insertPlaylist(Playlist playlist) {
        return insert(TABLE_NAME, null, getPlaylistContent(playlist));
    }

    /**
     * 更新一条收藏夹信息
     *
     * @param playlist
     */
    public int updatePlaylist(Playlist playlist) {
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = new String[]{playlist.getId() + ""};
        return update(TABLE_NAME, getPlaylistContent(playlist), whereClause, whereArgs);
    }

    /**
     * 删除一条收藏夹信息
     *
     * @param playlist
     */
    public void deletePlaylist(Playlist playlist) {
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = new String[]{playlist.getId() + ""};
        delete(TABLE_NAME, whereClause, whereArgs);
    }

    private Playlist getPlaylist(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        String coverUrl = cursor.getString(cursor.getColumnIndex(COLUMN_COVER_URL));
        int count = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));
        Playlist playlist = new Playlist(id, title, coverUrl, count, description);
        return playlist;
    }

    public ContentValues getPlaylistContent(Playlist playlist) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, playlist.getTitle());
        values.put(COLUMN_COVER_URL, playlist.getCoverUrl());
        values.put(COLUMN_DESCRIPTION, playlist.getDescription());
        values.put(COLUMN_COUNT, playlist.getCount());
        return values;
    }

}
