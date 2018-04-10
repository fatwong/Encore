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

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    Call<Album> getAlbumInfo(@Query("artist") String artist, @Query("album") String album);

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST) //&artist=artist
    Call<Artist> getArtistInfo(@Query("artist") String artist);

}
