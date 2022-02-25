package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.wangzheng.common.CommonKit;

/**
 * https://github.com/weidongjian/androidWheelView
 * Create by wangzheng on 2019/7/30
 */
public class R_view extends View {
    private String[] mItems = {
            "1记得年年淡淡的",
            "2记得年年淡淡的",
            "3记得年年淡淡的",

            "4记得年年淡淡的",

            "5记得年年淡淡的",
            "6记得年年淡淡的",
            "7记得年年淡淡的"
    };

    private TextPaint mPaint;

    private Camera camera;

    public R_view(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x33ff0000);

        mPaint = new TextPaint();
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setAntiAlias(true);

        camera = new Camera();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        offsetY = 0;
        mPaint.setTextSize(CommonKit.sp2px(18));

        for (int i = 0; i < mItems.length; i++) {
            splitDraw(canvas, i);
        }
    }

    Matrix matrix = new Matrix();

    private float offsetY = 0;

    private void splitDraw(Canvas canvas, int index) {
        final int count = mItems.length;

//        float ratio = 1f;
//        float angle = 0f;
//        int middleIndex = (count - 1) / 2;
//        if (index < middleIndex) {
//            ratio = index * 1f / middleIndex;
//            angle = 80 * (1 - ratio);
//        } else if (index > middleIndex) {
//            ratio = (index - middleIndex) * 1f / middleIndex;
//            angle = -80 * ratio;
//        }

        float ratio = index * 1f / count;

//        float angle = -90 * ratio;

        ratio = 1 - ratio;
        float angle = 90 * ratio;

        String text = mItems[index];
        drawItem(canvas, text, ratio, angle);
    }

    private void drawItem(Canvas canvas, String text, float ratio, float angle) {
        Rect bounds = textMeasure(text, ratio);

        canvas.save();
        applyMatrix(canvas, angle, bounds);
        drawText(canvas, text, bounds);
        canvas.restore();

        drawDebugLine(canvas, angle, bounds);

        float lineSpace = CommonKit.dip2px(15);
        ratio = (1-ratio) * 0.6f + 0.4f;
        lineSpace *= ratio;
        offsetY += lineSpace;
    }

    private void drawDebugLine(Canvas canvas, float angle, Rect bounds) {
        final int width = getWidth();
        final int startX = 0;
        canvas.drawLine(startX,
                offsetY,
                width,
                offsetY,
                mPaint);

        float lineHeight = getLineHeight(
                angle, bounds);
        offsetY = offsetY + lineHeight;
        canvas.drawLine(startX,
                offsetY,
                width,
                offsetY,
                mPaint);
    }

    public float getLineHeight(float angle, Rect bounds) {
        float lineHeight = bounds.height();
        double radians = Math.toRadians(angle);
        double cosRadians = Math.cos(radians);
        double height = cosRadians * lineHeight;
        return (float) height;
    }

    protected void drawText(Canvas canvas, String text, Rect bounds) {
        float width = bounds.width();
        int lineHeight = bounds.height();

        float x = (getWidth() - width) / 2;
        float baseLine = offsetY + lineHeight
                - bounds.bottom;
        canvas.drawText(text, x, baseLine, mPaint);
    }

    protected void applyMatrix(Canvas canvas, float angle, Rect bounds) {
        matrix.reset();

        camera.save();
        camera.rotate(angle, 0, 0);
        camera.getMatrix(matrix);
        camera.restore();

        int halfWidth = getWidth() / 2;
        matrix.preTranslate(-halfWidth, -offsetY);
        matrix.postTranslate(halfWidth, offsetY);

        canvas.setMatrix(matrix);
    }

    private Rect textMeasure(String text, float ratio) {
        int maxSize = CommonKit.sp2px(18);
        ratio = (1-ratio) * 0.07f + 0.93f;
        float size = maxSize * ratio;
        mPaint.setTextSize(size);

        final int start = 0;
        final int end = text.length();
        Rect bounds = new Rect();
        mPaint.getTextBounds(text,
                start, end,
                bounds);
        return bounds;
    }
}
