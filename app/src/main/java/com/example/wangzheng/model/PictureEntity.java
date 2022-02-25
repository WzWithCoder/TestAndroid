package com.example.wangzheng.model;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import java.io.Serializable;

/**
 * Create by wangzheng on 2018/3/23
 */

public class PictureEntity implements Serializable {
    public int drawableId;
    public float aspectRatio;

    public PictureEntity(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureEntity that = (PictureEntity) o;
        return drawableId == that.drawableId;
    }

    public static float aspectRatio(Resources resources, int id) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, id, opts);
        return opts.outHeight * 1f / opts.outWidth;
    }
}
