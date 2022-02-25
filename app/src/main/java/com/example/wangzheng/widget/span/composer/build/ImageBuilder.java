package com.example.wangzheng.widget.span.composer.build;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.widget.span.composer.ImageSpan;

public final class ImageBuilder extends Builder<ImageSpan> {
    public TextView anchor;
    public Drawable drawable;
    public String url;
    public int resId;

    public ImageBuilder anchor(TextView anchor) {
        this.anchor = anchor;
        return this;
    }

    public ImageBuilder drawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public ImageBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ImageBuilder resId(int id) {
        this.resId = id;
        return this;
    }

    public ImageBuilder height(float value) {
        this.size = CommonKit.dip2px(value);
        return this;
    }

    public ImageSpan build() {
        return new ImageSpan(this);
    }
}