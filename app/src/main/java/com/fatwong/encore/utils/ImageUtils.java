package com.fatwong.encore.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Created by Isaac on 2018/3/25.
 */

public class ImageUtils {
    public static void glideWith(Context context, String coverUrl, int resID, final ImageView imageView) {
        Glide.with(context)
                .load(coverUrl)
                .apply(RequestOptions.placeholderOf(resID))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                    }
                });
    }

    public static void glideWithRoundImage(Context context, String url,ImageView imageView){
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().transform(new GlideCircleTransform()))
                .into(imageView);
    }
}
