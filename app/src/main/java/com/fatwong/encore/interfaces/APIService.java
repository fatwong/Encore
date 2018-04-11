package com.fatwong.encore.interfaces;

import com.fatwong.encore.bean.Album;
import com.fatwong.encore.bean.Artist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface APIService {

    String BASE_PARAMETERS_ALBUM = "2.0/?method=album.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";
    String BASE_PARAMETERS_ARTIST = "2.0/?method=artist.getinfo&api_key=fdb3a51437d4281d4d64964d333531d4&format=json";
    String BASE_PARAMETERS_URL = "http://orp6z38cm.bkt.clouddn.com/";

    //需要maintain，暂时弃用
    String DYNAMIC_URL = "http://api.klm123.net/video/queryLabelVideoList.do?src=2000&labelid=46&refreshcount=3&pagesize=10&currentpage=";
    String MV_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.mv.searchMV&order=0&page_num=1&page_size=20";
    String MV_INFO_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.mv.playMV&mv_id=";
    String PLAYLIST_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.diy.gedan&page_size=60&page_no=1";
    String PLAYLIST_INFO_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.diy.gedanInfo&listid=";
    String SONG_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&songid=";

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    Call<Album> getAlbumInfo(@Query("artist") String artist, @Query("album") String album);

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST) //&artist=artist
    Call<Artist> getArtistInfo(@Query("artist") String artist);

}
