package com.fatwong.encore.net;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTServiceFactory {

    private static final long CACHE_SIZE = 1024 * 1024;

    public static <T> T create(Context context, final String baseUrl, Class<T> clazz) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(context.getApplicationContext().getCacheDir(), CACHE_SIZE))
                .connectTimeout(40, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Cache-Control", String.format("max-age=%d,max-stale=%d", Integer.valueOf(60 * 60 * 24 * 7), Integer.valueOf(31536000)))
                                .addHeader("Connection", "keep-alive")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);

    }

}
