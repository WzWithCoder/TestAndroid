package com.example.wangzheng.widget.span.composer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import com.example.wangzheng.widget.span.composer.build.Size;
import com.example.wangzheng.widget.span.composer.build.TextBuilder;


/**
 * Create by wangzheng on 2021/5/10
 */
public final class TextSpan implements AbsSpan {
    private String text;
    private float textSize = 1f;
    private int textColor;
    private boolean fakeBold;
    private float align = 0.5f;
    private boolean strikeLine;
    private float strokeWidth;
    private int leftMargin, rightMargin,
            topMargin, bottomMargin;

    private int mWidth, mHeight;
    private Paint mPaint;
    private Rect mTextRect;
    private Size mSize;

    public static TextBuilder newBuilder() {
        return new TextBuilder();
    }

    public TextSpan(TextBuilder builder) {
        this.text = builder.text;
        this.textSize = builder.size;
        this.strokeWidth = builder.strokeWidth;
        this.textColor = builder.textColor;
        this.fakeBold = builder.fakeBold;
        this.align = builder.align;
        this.strikeLine = builder.strikeLine;
        this.topMargin = builder.topMargin;
        this.bottomMargin = builder.bottomMargin;
        this.leftMargin = builder.leftMargin;
        this.rightMargin = builder.rightMargin;

        mPaint = new TextPaint();
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        mPaint.setFakeBoldText(fakeBold);
        if (strokeWidth > 0) {
            applyStrokeWidth();
        }
        if (strikeLine) {
            applyStrikeLine();
        }
    }

    private final void applyStrokeWidth() {
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(strokeWidth);
    }

    private final void applyStrikeLine() {
        int flags = Paint.ANTI_ALIAS_FLAG |
                Paint.STRIKE_THRU_TEXT_FLAG;
        mPaint.setFlags(flags);
    }

    @Override
    public final Size measure(int height) {
        mTextRect = textMeasure(text, mPaint);
        mWidth = mTextRect.width() +
                leftMargin + rightMargin;
        mHeight = mTextRect.height() +
                topMargin + bottomMargin;
        mSize = Size.with(mWidth, mHeight);
        return mSize;
    }

    @Override
    public void draw(Canvas canvas, RectF rect) {
        //基于基线的offset偏移量
        float diffHeight = rect.height() - mHeight;
        float alignOffset = diffHeight * align;
        float baseLine = rect.bottom - mTextRect.bottom;
        float dy = baseLine - alignOffset - bottomMargin;
        float dx = leftMargin - mTextRect.left;
        //绘制文本
        canvas.drawText(text, dx, dy, mPaint);
        //测试 渲染边框
        debugDraw(canvas, mTextRect, dx, dy);
    }

    @Override
    public Size size() {
        return mSize;
    }
}
