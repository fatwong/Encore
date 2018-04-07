package com.fatwong.encore.db.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.fatwong.encore.bean.PlaylistRelation;

import java.util.ArrayList;
import java.util.List;

public class PlaylistRelationDao extends BaseDao{

    private static final String TABLE_NAME = "PLAYLIST_RELATION";

    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_PLAYLIST_ID = "playlist_id";
    private final static String COLUMN_SONG_ID = "song_id";

    /**
     * 建表sql
     *
     * @return sql
     */
    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(");
        sb.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append(COLUMN_PLAYLIST_ID + " INTEGER,");
        sb.append(COLUMN_SONG_ID + " INTEGER,");
        sb.append("unique (" + COLUMN_PLAYLIST_ID + "," + COLUMN_SONG_ID + ")");
        sb.append(");");
        return sb.toString();
    }

    /**
     * 获取收藏夹上的所有歌曲
     *
     * @return playlistRelationList
     */
    public List<PlaylistRelation> queryByPlaylistId(int playlistId) {
        String selection = COLUMN_PLAYLIST_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(playlistId)};
        List<PlaylistRelation> playlistRelationList = new ArrayList<>();
        Cursor cursor = query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            playlistRelationList.add(getPlaylistRelation(cursor));
        }
        cursor.close();
        return playlistRelationList;
    }

    public PlaylistRelation queryByPlaylistIdAndSongId(int playlistId, int songId){
        String selection = COLUMN_PLAYLIST_ID + "=? and "+ COLUMN_SONG_ID +"=?";
        String[] selectionArgs = new String[]{String.valueOf(playlistId),String.valueOf(songId)};
        Cursor cursor = query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
        PlaylistRelation relation = null;
        if(cursor.moveToNext()){
            relation = getPlaylistRelation(cursor);
        }
        cursor.close();
        return relation;
    }

    /**
     * 插入一条收藏夹关联记录
     *
     * @param playlistRelation
     */
    public long insertPlaylistRelation(PlaylistRelation playlistRelation) {
        return insert(TABLE_NAME, null, getPlaylistRelationContent(playlistRelation));
    }

    /**
     * 更新一条收藏夹关联信息
     *
     * @param playlistRelation
     */
    public int updatePlaylistRelation(PlaylistRelation playlistRelation) {
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = new String[]{playlistRelation.getId() + ""};
        return update(TABLE_NAME, getPlaylistRelationContent(playlistRelation), whereClause, whereArgs);
    }

    /**
     * 删除一条收藏夹关联信息
     *
     * @param id
     */
    public int deletePlaylistRelation(int id) {
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = new String[]{id + ""};
        return delete(TABLE_NAME, whereClause, whereArgs);
    }

    /**
     * 删除一条收藏夹关联信息
     *
     * @param playlistId
     * @param songId
     * @return
     */
    public int deletePlaylistRelation(int playlistId, int songId) {
        String whereClause = COLUMN_PLAYLIST_ID + "=? and "+ COLUMN_SONG_ID +"=?";
        String[] whereArgs = new String[]{String.valueOf(playlistId),String.valueOf(songId)};
        return delete(TABLE_NAME, whereClause, whereArgs);
    }


    private PlaylistRelation getPlaylistRelation(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        int playlistId = cursor.getInt(cursor.getColumnIndex(COLUMN_PLAYLIST_ID));
        int songId = cursor.getInt(cursor.getColumnIndex(COLUMN_SONG_ID));
        PlaylistRelation relation = new PlaylistRelation(id, playlistId, songId);
        return relation;
    }

    public ContentValues getPlaylistRelationContent(PlaylistRelation collectionShip) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYLIST_ID, collectionShip.getPlaylistId());
        values.put(COLUMN_SONG_ID, collectionShip.getSongId());
        return values;
    }

}
