package com.fatwong.encore.net;

import android.content.Context;

import com.fatwong.encore.bean.AlbumInfo;
import com.fatwong.encore.bean.AlbumQuery;
import com.fatwong.encore.bean.Artist;
import com.fatwong.encore.bean.ArtistQuery;
import com.fatwong.encore.interfaces.APIService;
import com.fatwong.encore.interfaces.AlbumInfoListener;
import com.fatwong.encore.interfaces.ArtistInfoListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastfmClient {

    private static final String BASE_API_URL = "http://ws.audioscrobbler.com";
    private static LastfmClient instance;
    private APIService apiService;

    public static LastfmClient getInstance(Context context) {
        if (instance == null) {
            instance = new LastfmClient();
        }
        instance.apiService = RESTServiceFactory.create(context, BASE_API_URL, APIService.class);
        return instance;
    }

    public void getArtistInfo(ArtistQuery artistQuery, final ArtistInfoListener listener) {
        Call<Artist> call = apiService.getArtistInfo(artistQuery.mArtist);
        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {
                listener.artistInfoSucess(response.body().mArtist);
            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {
                listener.artistInfoFailed();
            }
        });
    }

    public void getAlbumInfo(AlbumQuery albumQuery, final AlbumInfoListener listener) {
        Call<AlbumInfo> call = apiService.getAlbumInfo(albumQuery.mArtist, albumQuery.mAlbum);
        call.enqueue(new Callback<AlbumInfo>() {
            @Override
            public void onResponse(Call<AlbumInfo> call, Response<AlbumInfo> response) {
                listener.albumInfoSuccess(response.body().mAlbum);
            }

            @Override
            public void onFailure(Call<AlbumInfo> call, Throwable t) {
                listener.albumInfoFailed();
            }
        });
    }


}
