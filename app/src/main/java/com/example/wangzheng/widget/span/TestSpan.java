package com.example.wangzheng.widget.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;

import java.util.Arrays;

public class TestSpan extends ReplacementSpan implements ParagraphStyle {
    private int bWidth, bHeight;
    private float ratio = 1f;
    private final Paint mBgPaint;
    private final TextPaint mTextPaint;
    private int vPadding, hPadding,
            leftMargin, rightMargin;
    private Rect mTextRect;
    private final float[] radius = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

    public TestSpan() {
        this.ratio = 0.8f;
        this.hPadding = 2;
        this.vPadding = 1;
        Arrays.fill(radius, 2);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth(1f);
        mBgPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
    }

    private Rect textMesure(Paint paint
            , CharSequence text, int start, int end) {
        final Rect rect = new Rect();
        String str = text.toString();
        paint.getTextBounds(str,
                start, end,
                rect);
        return rect;
    }

    @Override
    public int getSize(Paint paint, CharSequence text
            , int start, int end, Paint.FontMetricsInt fm) {
        mTextPaint.setTextSize(paint.getTextSize() * ratio);
        mTextRect = textMesure(mTextPaint, text, start, end);
        bWidth  = mTextRect.width() + hPadding * 2;
        bHeight = mTextRect.height() + vPadding * 2;
        float strokeWidth = mBgPaint.getStrokeWidth();
        return bWidth + leftMargin + rightMargin +
                (int) strokeWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end
            , float x, int top, int y, int bottom, Paint paint) {
        //原始文字的高度尺寸
        Rect textRect = textMesure(paint, text, start, end);
        textRect.offset(Math.round(x), y);
        float textHeight = textRect.height();
        //绘制背景
        float offsetY = (textHeight - bHeight) / 2f;
        float dy = textRect.top + offsetY;
        float strokeWidth = mBgPaint.getStrokeWidth();
        float dx = x + leftMargin + strokeWidth / 2f;
        RectF rectF = drawBackground(canvas, dx, dy);
        //绘制文字
        drawTextOffset(canvas, rectF, text, start, end);
    }

    private void drawTextOffset(Canvas canvas, RectF rectF
            , CharSequence text, int start, int end) {
        //基于基线的offset偏移量
        float offset = (rectF.height() - mTextRect.height()) * 0.5f;
        float baseLine = rectF.bottom - mTextRect.bottom - offset;
        //画一个文字
        canvas.drawText(text, start, end,
                rectF.centerX(),
                baseLine,
                mTextPaint);
    }

    private void drawTextCneter(Canvas canvas, RectF rectF
            , CharSequence text, int start, int end) {
        //边框高度的一半 - 文字总高度的一半
        float baseLine = (
                rectF.bottom + rectF.top -
                mTextPaint.descent() -
                mTextPaint.ascent()
        ) / 2;
        //居中画一个文字
        canvas.drawText(text, start, end,
                rectF.centerX(),
                baseLine,
                mTextPaint);
    }

    private RectF drawBackground(Canvas canvas, float dx, float dy) {
        final float left = 0, top = 0;
        RectF rect = new RectF(
                left, top,
                bWidth,
                bHeight);
        rect.offset(dx, dy);
        Path roundPath = obtainBgPath(rect);
        canvas.drawPath(roundPath, mBgPaint);
        return rect;
    }

    private Path obtainBgPath(RectF rectF) {
        Path path = new Path();
        path.addRoundRect(rectF,
                radius,
                Path.Direction.CW);
        path.close();
        return path;
    }

    public TestSpan setStyle(Paint.Style style) {
        mBgPaint.setStyle(style);
        return this;
    }

    public TestSpan setBackgroundColor(int backgroundColor) {
        mBgPaint.setColor(backgroundColor);
        return this;
    }

    public TestSpan setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        return this;
    }

    public TestSpan setFakeBoldText(boolean fakeBoldText) {
        mTextPaint.setFakeBoldText(fakeBoldText);
        return this;
    }

    public TestSpan setPadding(int horizontalPadding, int verticalPadding) {
        this.hPadding = horizontalPadding;
        this.vPadding = verticalPadding;
        return this;
    }

    public TestSpan setStrokeWidth(float strokeWidth) {
        mBgPaint.setStrokeWidth(strokeWidth);
        return this;
    }

    public TestSpan setCorner(int corner) {
        Arrays.fill(radius, corner);
        return this;
    }

    public TestSpan setRadius(float leftRadius, float rightRadius){
        radius[0] = radius[1] = radius[6] = radius[7] = leftRadius;
        radius[2] = radius[3] = radius[4] = radius[5] = rightRadius;
        return this;
    }

    public TestSpan setLeftBottomRadius(float r){
        radius[6] = radius[7] = r;
        return this;
    }

    public TestSpan setRatio(float ratio) {
        this.ratio = ratio;
        return this;
    }

    public TestSpan setMargin(int leftMargin, int rightMargin) {
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        return this;
    }
}
