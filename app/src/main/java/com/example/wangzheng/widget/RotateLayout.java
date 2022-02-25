package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 区间-90<-->90
 * Create by wangzheng on 3/4/21
 */
public class RotateLayout extends LinearLayout {
    public RotateLayout(@NonNull Context context) {
        this(context, null);
    }

    public RotateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (canResizeLayout()) {
            resizeLayout();
        }
    }

    private final void resizeLayout() {
        final int mwidth  = getMeasuredWidth();
        final int mheight = getMeasuredHeight();

        final View child = getChildAt(0);
        float rotation = child.getRotation();
        float angle = Math.abs(rotation);

        final double α = Math.toRadians(angle);
        final double cosα = Math.cos(α);
        final double sinα = Math.sin(α);

        double width  = mwidth * cosα + mheight * sinα;
        double height = mheight * cosα + mwidth * sinα;

        setMeasuredDimension((int)width, (int)height);
    }

    private boolean canResizeLayout() {
        View child = getChildAt(0);
        if (child == null) return false;
        float rotate = child.getRotation();
        return rotate != 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG |
                Paint.DITHER_FLAG |
                Paint.FILTER_BITMAP_FLAG);
        int childCount = getChildCount();
        View childView = null;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            childView.setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
    }

    public static void fixAntiAlias(View viewFromThe80s) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        viewFromThe80s.setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
        ((View) viewFromThe80s.getParent()).setLayerType(View.LAYER_TYPE_SOFTWARE, paint);
    }
}
