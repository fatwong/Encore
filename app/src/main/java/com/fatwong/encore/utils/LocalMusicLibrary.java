package com.fatwong.encore.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.fatwong.encore.bean.Album;
import com.fatwong.encore.bean.Artist;
import com.fatwong.encore.bean.Song;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地音乐库
 * Created by Isaac on 2018/3/6.
 */

public class LocalMusicLibrary {

    private static String[] proj_music = new String[]{
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE};
    private static String[] proj_album = new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.NUMBER_OF_SONGS, MediaStore.Audio.Albums.ARTIST};
    private static String[] proj_artist = new String[]{
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
            MediaStore.Audio.Artists._ID};

    public static Artist getArtist(Context context, long artistId) {
        List<Artist> artistList = getAllArtists(context);
        for (Artist artist: artistList) {
            if (artist.artist_id == artistId) {
                return artist;
            }
        }
        return null;
    }

    public static Album getAlbum(Context context, long albumId) {
        List<Album> albumList = getAllAlbums(context);
        for (Album album: albumList) {
            if (album.album_id == albumId) {
                return album;
            }
        }
        return null;
    }

    /**
     * 获取所有本地歌曲
     * @param context
     * @return
     */
    public static List<Song> getAllSongs(Context context) {
        ArrayList<Song> songs = new ArrayList<>();
        String selectionStatement = "is_music=1 AND title !=''";
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id","title","artist","album","duration","track","artist_id","album_id","_data"},
                selectionStatement, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if((cursor != null) && cursor.moveToFirst())
            do {
                Song song = getSongFromCursor(cursor);
                if(song.isStatus()){
                    songs.add(song);
                }

            }
            while (cursor.moveToNext());
        if(cursor != null){
            cursor.close();
        }
        return songs;
    }

    public static List<Artist> getAllArtists(Context context) {
        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        StringBuilder where = new StringBuilder(MediaStore.Audio.Artists._ID
                + " in (select distinct " + MediaStore.Audio.Media.ARTIST_ID
                + " from audio_meta where (1=1 )");

        where.append(")");

        List<Artist> list = getArtistListFromCursor(cr.query(uri, proj_artist,
                where.toString(), null, PreferencesUtility.getInstance(context).getArtistSortOrder()));

        return list;

    }

    /**
     * 获取专辑信息
     *
     * @param context
     * @return
     */
    public static List<Album> getAllAlbums(Context context) {

        ContentResolver cr = context.getContentResolver();
        StringBuilder where = new StringBuilder(MediaStore.Audio.Albums._ID
                + " in (select distinct " + MediaStore.Audio.Media.ALBUM_ID
                + " from audio_meta where (1=1)");

        where.append(" )");

        // Media.ALBUM_KEY 按专辑名称排序
        List<Album> list = getAlbumListFromCursor(cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, proj_album,
                where.toString(), null, PreferencesUtility.getInstance(context).getAlbumSortOrder()));
        return list;

    }

    public static List<Song> getArtistAllSongs(Context context, int artistId) {
        ArrayList<Song> songs = new ArrayList<>();
        StringBuilder selectionStatement = new StringBuilder("is_music=1 AND title !=''");
        selectionStatement.append(" and " + MediaStore.Audio.Media.ARTIST_ID + " = " + artistId);
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id","title","artist","album","duration","track","artist_id","album_id","_data"},
                selectionStatement.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if((cursor != null) && cursor.moveToFirst())
            do {
                Song song = getSongFromCursor(cursor);
                if(song.isStatus()){
                    songs.add(song);
                }

            }
            while (cursor.moveToNext());
        if(cursor != null){
            cursor.close();
        }
        return songs;
    }

    public static List<Song> getAlbumAllSongs(Context context, int albumId) {
        ArrayList<Song> songs = new ArrayList<>();
        StringBuilder selectionStatement = new StringBuilder("is_music=1 AND title !=''");
        selectionStatement.append(" and " + MediaStore.Audio.Media.ALBUM_ID + " = " + albumId);
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"_id","title","artist","album","duration","track","artist_id","album_id","_data"},
                selectionStatement.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if((cursor != null) && cursor.moveToFirst())
            do {
                Song song = getSongFromCursor(cursor);
                if(song.isStatus()){
                    songs.add(song);
                }

            }
            while (cursor.moveToNext());
        if(cursor != null){
            cursor.close();
        }
        return songs;
    }

    public static Song getSongFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
        int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        int trackNumber = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK));
        long artistId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID));
        long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
        String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        Song song = new Song();
        song.setId(-id);
        song.setTitle(title);
        song.setAlbumName(album);
        song.setArtistName(artist);
        song.setDuration(duration);
        song.setTrackNumber(trackNumber);
        song.setArtistId(artistId);
        song.setAlbumId(albumId);
        String cover = getAlbumArtUri(albumId).toString();
        song.setCoverUrl(cover);
        song.setPath(url);
        song.setUrl(url);
        if (FileUtils.existFile(url)) {
            song.setStatus(true);
        }
        return song;
    }

    public static List<Album> getAlbumListFromCursor(Cursor cursor) {
        List<Album> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Album info = new Album();
            info.album_name = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Albums.ALBUM));
            info.album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
            info.number_of_songs = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
            info.album_art = getAlbumArtUri(info.album_id) + "";
            info.album_artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
            info.album_sort = Pinyin.toPinyin(info.album_name.charAt(0)).substring(0, 1).toUpperCase();
            list.add(info);
        }
        cursor.close();
        return list;
    }

    public static List<Artist> getArtistListFromCursor(Cursor cursor) {
        List<Artist> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Artist info = new Artist();
            info.artist_name = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Artists.ARTIST));
            info.number_of_tracks = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
            info.artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
            info.artist_sort = Pinyin.toPinyin(info.artist_name.charAt(0)).substring(0, 1).toUpperCase();
            list.add(info);
        }
        cursor.close();
        return list;
    }

    private static Uri getAlbumArtUri(long param){
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),param);
    }
}
