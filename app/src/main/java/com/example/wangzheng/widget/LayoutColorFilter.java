package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Create by wangzheng on 2021/5/18
 */
public final class LayoutColorFilter extends LinearLayout {

    private Paint paint;
    private int color = -1;

    public LayoutColorFilter(@NonNull Context context) {
        this(context, null);
    }

    public LayoutColorFilter(@NonNull Context context
            , @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayoutColorFilter(@NonNull Context context
            , AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
        Xfermode xfermode = new PorterDuffXfermode(mode);
        paint.setXfermode(xfermode);
        paint.setColor(color);
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (color == -1) {
            super.draw(canvas);
        } else filter(canvas, c-> {
            super.draw(canvas);
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (color == -1) {
            super.dispatchDraw(canvas);
        } else filter(canvas, c-> {
            super.dispatchDraw(canvas);
        });
    }

    private void filter(Canvas canvas, Handle<Canvas> draw) {
        int saveCount = saveLayer(canvas);
        draw.invoke(canvas);
        drawLayer(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void drawLayer(Canvas canvas) {
        Rect bounds = new Rect();
        bounds.right = canvas.getWidth();
        bounds.bottom = canvas.getHeight();
        canvas.drawRect(bounds, paint);
    }

    private final int saveLayer(Canvas canvas) {
        final Paint paint = null;
        RectF bounds = new RectF();
        bounds.right = canvas.getWidth();
        bounds.bottom = canvas.getHeight();
        int saveFlags = Canvas.ALL_SAVE_FLAG;
        return canvas.saveLayer(
                bounds,
                paint,//may be null
                saveFlags);
    }

    private interface Handle<T> {
        void invoke(T t);
    }
}
