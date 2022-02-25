package com.example.wangzheng.widget.span.composer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wangzheng.App;
import com.example.wangzheng.widget.span.ImageSpanTarget;
import com.example.wangzheng.widget.span.composer.build.ImageBuilder;
import com.example.wangzheng.widget.span.composer.build.Size;

import static com.example.wangzheng.common.BitmapKit.drawableToBitmap;


/**
 * Create by wangzheng on 2021/5/10
 */
public final class ImageSpan implements AbsSpan {
    private TextView anchor;
    private String url;
    private int resId;
    private float size = 1f;
    private float align = 0.5f;
    private float[] radius;
    private boolean fitHeight;
    private int leftMargin, rightMargin,
            topMargin, bottomMargin;

    private Paint mPaint;
    private Size mSize;
    private Drawable mDrawable;
    private int mHeight;

    public static ImageBuilder newBuilder() {
        return new ImageBuilder();
    }

    public ImageSpan(ImageBuilder builder) {
        this.anchor = builder.anchor;
        this.url = builder.url;
        this.resId = builder.resId;
        this.size = builder.size;
        this.align = 1 - builder.align;
        this.radius = builder.radius;
        this.fitHeight = builder.fitHeight;
        this.topMargin = builder.topMargin;
        this.bottomMargin = builder.bottomMargin;
        this.leftMargin = builder.leftMargin;
        this.rightMargin = builder.rightMargin;
        this.mDrawable = builder.drawable;

        if (mDrawable != null) {
            builder.drawable(mDrawable);
        } else if (resId > 0) {
            loadDrawable();
        } else {
            loadUrlImage(builder);
        }
    }

    private void loadUrlImage(ImageBuilder builder) {
        Context context = anchor.getContext();
        Glide.with(context).load(url).into(
            new ImageSpanTarget(anchor) {
                public void handle(Drawable drawable) {
                    builder.drawable(drawable);
                    mDrawable = drawable;
                }
            });
    }

    private void loadDrawable() {
        Context context = App.instance();
        Resources resources = context.getResources();
        mDrawable = resources.getDrawable(resId);
    }

    @Override
    public final Size measure(int height) {
        if (mDrawable == null) {
            return mSize = Size.empty();
        }

        int dwidth = mDrawable.getIntrinsicWidth();
        int dheight = mDrawable.getIntrinsicHeight();

        int vheight = fitHeight && height > 0
                ? height : (int) size;
        int vwidth = dwidth * vheight / dheight;

        int bwidth  = vwidth + leftMargin + rightMargin;
        int bheight = vheight + topMargin + bottomMargin;

        this.mHeight = bheight;
        mDrawable.setBounds(0, 0,
                vwidth,
                vheight);

        return mSize = Size.with(bwidth, bheight);
    }

    @Override
    public void draw(Canvas canvas, RectF rect) {
        if (mDrawable == null) return;

        float diffHeight = rect.height() - mHeight;
        float alignOffset = diffHeight * align;
        float dy = alignOffset + rect.top + topMargin;
        float dx = leftMargin;

        int saveCount = canvas.save();
        canvas.translate(dx, dy);
        if (radius == null) {
            mDrawable.draw(canvas);
        } else {
            updateBitmapShader();
            drawRoundDrawable(canvas);
        }
        canvas.restoreToCount(saveCount);
    }

    private void drawRoundDrawable(Canvas canvas) {
        if (mPaint == null) return;
        Rect rect = mDrawable.getBounds();
        Path path = obtainPath(rect);
        canvas.drawPath(path, mPaint);

        //测试 渲染边框
        debugDraw(canvas, rect, 0, 0);
    }

    private Path obtainPath(Rect rect) {
        Path path = new Path();
        RectF rectF = new RectF(rect);
        path.addRoundRect(rectF,
                radius,
                Path.Direction.CW);
        path.close();
        return path;
    }

    private void updateBitmapShader() {
        if (mPaint != null) return;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        Rect bounds = mDrawable.getBounds();
        Bitmap bitmap = drawableToBitmap(
                mDrawable,
                bounds.width(),
                bounds.height());
        Shader shader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
    }

    @Override
    public Size size() {
        return mSize;
    }
}
