package com.example.wangzheng.widget.span;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Create by wangzheng on 2/7/21
 */
public abstract class ImageSpanTarget extends SimpleTarget<GlideDrawable> implements Drawable.Callback {
    private Drawable drawable;
    private TextView anchor;

    public ImageSpanTarget(TextView anchor) {
        this.anchor = anchor;
    }

    public abstract void handle(Drawable drawable);

    @Override
    public void onResourceReady(GlideDrawable drawable
            , GlideAnimation<? super GlideDrawable> transition) {
        if (this.drawable instanceof GifDrawable) return;
        if (drawable == null) return;

        this.drawable = drawable;
        handle(drawable);
        drawable.setCallback(this);
        drawable.invalidateSelf();
        anchor.setText(anchor.getText());
        onStart();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (drawable instanceof GifDrawable) {
            ((GifDrawable) drawable).start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (drawable instanceof GifDrawable) {
            ((GifDrawable) drawable).stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (drawable instanceof GifDrawable) {
            //((GifDrawable) drawable).recycle();
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        if (drawable == null) return;
        anchor.invalidate(drawable.getBounds());
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        if (drawable == null) return;
        anchor.invalidate(drawable.getBounds());
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }
}
