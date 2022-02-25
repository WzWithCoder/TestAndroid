package com.example.wangzheng.album;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.model.PictureEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Builder implements Serializable {
    public List<PictureEntity> pictures;
    public transient RectF rect;
    public int index;
    public int hSpacing;
    public int vSpacing;
    public int numColumns = 1;
    public boolean fullScreen = false;
    public int statusBarHeight;

    public Builder pictures(List<PictureEntity> pictures) {
        this.pictures = pictures;
        return this;
    }

    public Builder rect(RectF rect) {
        this.rect = rect;
        return this;
    }

    public Builder index(int index) {
        this.index = index;
        return this;
    }

    public Builder numColumns(int numColumns) {
        this.numColumns = numColumns;
        return this;
    }

    public Builder hSpacing(int hSpacing) {
        this.hSpacing = hSpacing;
        return this;
    }

    public Builder vSpacing(int vSpacing) {
        this.vSpacing = vSpacing;
        return this;
    }

    public Builder fullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        return this;
    }

    public Builder addDrawableId(Resources resources, int id) {
        if (pictures == null) {
            pictures = new ArrayList<>();
        }
        PictureEntity picture = new PictureEntity(id);
        picture.aspectRatio = PictureEntity.aspectRatio(resources, id);
        pictures.add(picture);
        return this;
    }

    public RectF outRect(Context context, int index) {
        PictureEntity picture = pictures.get(index);
        DisplayMetrics display = context.getResources().getDisplayMetrics();

        float screenHeight = display.heightPixels;
        float outWidth = display.widthPixels;
        float outHeight = picture.aspectRatio * outWidth;
        float top = (screenHeight - outHeight) / 2;

        RectF rect = new RectF(0, top, outWidth, screenHeight - top);
        if (!fullScreen) {
            rect.offset(0, -statusBarHeight);
        }
        return rect;
    }

    public RectF inRect(int position) {
        int offsetRow = index2Row(position) - index2Row(index);
        int offsetColumn = index2Column(position) - index2Column(index);
        float dx = offsetColumn * rect.width() + offsetColumn * hSpacing;
        float dy = offsetRow * rect.height() + offsetRow * vSpacing;
        RectF rf = new RectF(rect);
        rf.offset(dx, dy);
        return rf;
    }

    private int index2Row(int index) {
        return index / numColumns;
    }

    private int index2Column(int index) {
        return index % numColumns;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder from(Activity context) {
        Intent intent = context.getIntent();
        Builder builder = (Builder) intent.getSerializableExtra(BUILDER);
        RectF rect = intent.getParcelableExtra(RECT);
        builder.rect(rect);
        return builder;
    }

    public void build(Activity context) {
        statusBarHeight = CommonKit.getStatusBarHeight();
        if (!fullScreen) {
            rect.offset(0, -statusBarHeight);
        }
        context.overridePendingTransition(0, 0);
        Intent intent = new Intent(context, BrowsePictureActivity.class);
        intent.putExtra(BUILDER, this);
        intent.putExtra(RECT, rect);
        context.startActivity(intent);
    }

    private final static String BUILDER = "BUILDER";
    private final static String RECT = "RECT";
}