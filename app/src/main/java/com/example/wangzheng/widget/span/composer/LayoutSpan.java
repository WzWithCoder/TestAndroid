package com.example.wangzheng.widget.span.composer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;

import com.example.wangzheng.widget.span.composer.build.Builder;
import com.example.wangzheng.widget.span.composer.build.ImageBuilder;
import com.example.wangzheng.widget.span.composer.build.Size;


/**
 * Create by wangzheng on 2021/5/10
 */
public final class LayoutSpan implements AbsSpan {
    private View anchor;
    private float align = 0.5f;
    private boolean fitHeight;
    private int leftMargin, rightMargin,
            topMargin, bottomMargin;

    private Size mSize;
    private int mHeight;

//    public static Builder newBuilder() {
//        return new ImageBuilder();
//    }

    public LayoutSpan(Builder builder, View anchor) {
        this.align = 1 - builder.align;
        this.fitHeight = builder.fitHeight;
        this.topMargin = builder.topMargin;
        this.bottomMargin = builder.bottomMargin;
        this.leftMargin = builder.leftMargin;
        this.rightMargin = builder.rightMargin;

        this.anchor = anchor;
    }

    @Override
    public final Size measure(int height) {
        int vheight = fitHeight && height > 0 ?
                height : anchor.getHeight();
        int vwidth = anchor.getWidth();

        int bwidth  = vwidth + leftMargin + rightMargin;
        int bheight = vheight + topMargin + bottomMargin;

        this.mHeight = bheight;

        return mSize = Size.with(bwidth, bheight);
    }

    @Override
    public void draw(Canvas canvas, RectF rect) {
        float diffHeight = rect.height() - mHeight;
        float alignOffset = diffHeight * align;
        float dy = alignOffset + rect.top + topMargin;
        float dx = leftMargin;

        FrameLayout.LayoutParams layoutParams = null;
        layoutParams = (FrameLayout.LayoutParams)
                anchor.getLayoutParams();
        layoutParams.leftMargin = (int) dx;
        layoutParams.topMargin  = (int) dy;
    }

    @Override
    public Size size() {
        return mSize;
    }
}
