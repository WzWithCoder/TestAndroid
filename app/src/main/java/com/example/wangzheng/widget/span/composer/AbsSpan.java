package com.example.wangzheng.widget.span.composer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.wangzheng.widget.span.composer.build.Size;

/**
 * Create by wangzheng on 2021/5/14
 */
public interface AbsSpan {
    Size measure(int height);

    void draw(Canvas canvas, RectF rect);

    Size size();

    default Rect textMeasure(String text, Paint paint) {
        final Rect rect = new Rect();
        final int start = 0;
        final int end = text.length();
        paint.getTextBounds(text,
                start, end,
                rect);
        return rect;
    }

    default RectF textMeasure(CharSequence text
            , int start, int end, Paint paint) {
        String sequence = text.subSequence(
                start, end).toString();
        Rect rect = textMeasure(
                sequence,
                paint);
        rect.offset(-rect.left,0);
        return new RectF(rect);
    }

    static boolean isDebug = false;

    default void debugDraw(Canvas canvas, Rect rect, float dx, float dy) {
        if (isDebug) return;
        RectF border = new RectF(rect);
        debugDraw(canvas, border, dx, dy);
    }

    default void debugDraw(Canvas canvas, RectF rect, float dx, float dy) {
        if (isDebug) return;
        RectF border = new RectF(rect);
        border.offset(dx, dy);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(border, paint);
    }
}
