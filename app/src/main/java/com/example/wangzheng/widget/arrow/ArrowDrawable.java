package com.example.wangzheng.widget.arrow;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Create by wangzheng on 2018/12/24
 */
public class ArrowDrawable extends Drawable {
    private Paint mPaint;
    private Path  mPath;
    private int   mWidth;
    private int   mHeight;
    private int   mOrientation;
    private float mInnerPadding;

    public static ArrowDrawable valueOf(
            int color,
            int orientation,
            Paint.Style style,
            float strokeWidth,
            int width,
            int height) {
        return new ArrowDrawable()
                .color(color)
                .orientation(orientation)
                .style(style)
                .strokeWidth(strokeWidth)
                .width(width)
                .height(height);
    }

    public ArrowDrawable() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //CornerPathEffect cornerEffect
        // = new CornerPathEffect(radius);
        //mPaint.setPathEffect(cornerEffect);
        mPath = new Path();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        setBounds(0,0, mWidth, mHeight);
        mPath.reset();
        mPath.moveTo(mInnerPadding, mInnerPadding);
        if (mOrientation == 2 || mOrientation == 3) {
            //水平指向箭头
            mPath.lineTo(mWidth - mInnerPadding, mHeight / 2f);
            mPath.lineTo(mInnerPadding, mHeight - mInnerPadding);
        } else {//垂直指向箭头
            mPath.lineTo(mWidth / 2f, mHeight - mInnerPadding);
            mPath.lineTo(mWidth - mInnerPadding, mInnerPadding);
        }

        if (mPaint.getStyle() != Paint.Style.STROKE) {
            mPath.lineTo(mInnerPadding, mInnerPadding);
            mPath.close();
        }
        canvas.save();
        if (mOrientation == 0 || mOrientation == 3) {
            canvas.rotate(180, mWidth / 2f, mHeight / 2f);
        }
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    public ArrowDrawable style(Paint.Style style) {
        mPaint.setStyle(style);
        return this;
    }

    public ArrowDrawable color(int color) {
        mPaint.setColor(color);
        return this;
    }

    public ArrowDrawable orientation(int orientation) {
        this.mOrientation = orientation;
        return this;
    }

    public int orientation() {
        return mOrientation;
    }

    public ArrowDrawable strokeWidth(float width) {
        mPaint.setStrokeWidth(width);
        mInnerPadding = width / 2;
        return this;
    }

    public ArrowDrawable width(int width) {
        this.mWidth = width;
        return this;
    }

    public ArrowDrawable height(int height) {
        this.mHeight = height;
        return this;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha() < 255 ?
                PixelFormat.TRANSLUCENT :
                PixelFormat.OPAQUE;
    }
}
