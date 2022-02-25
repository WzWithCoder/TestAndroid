package com.example.wangzheng.widget.span.composer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;

import com.example.wangzheng.widget.span.composer.build.Builder;
import com.example.wangzheng.widget.span.composer.build.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wangzheng on 2021/5/10
 */
public class SpanComposer extends ReplacementSpan implements ParagraphStyle, AbsSpan {
    private List<AbsSpan> mChilds = new ArrayList<>();
    private List<Builder> mNodes;

    private int bWidth, bHeight;
    private final Paint mPaint;
    private Size mSize;

    private boolean fitHeight;
    private float borderSize;
    private int color;
    private float align = 0.5f;
    private float[] radius;
    private int vPadding, hPadding;
    private int leftMargin, rightMargin;

    public SpanComposer(Builder builder) {
        this.radius = builder.radius;
        this.borderSize = builder.borderSize;
        this.color = builder.bgColor;
        this.align = 1 - builder.align;
        this.fitHeight = builder.fitHeight;
        this.vPadding = builder.vPadding;
        this.hPadding = builder.hPadding;
        this.leftMargin = builder.leftMargin;
        this.rightMargin = builder.rightMargin;
        this.mNodes = builder.nodes;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(borderSize);
        mPaint.setStyle(builder.style);
        mPaint.setColor(color);
    }

    @Override
    public int getSize(Paint paint, CharSequence text
            , int start, int end
            , Paint.FontMetricsInt fm) {
        final int maxHeight = -1;
        mSize = measure(maxHeight);
        updateHeight(paint, text, start, end, fm);
        return mSize.width;
    }

    private void updateHeight(Paint paint, CharSequence text
            , int start, int end, Paint.FontMetricsInt fm) {
        if (fm == null) return;
        RectF textRect = textMeasure(text,
                start, end,
                paint);
        RectF rect = caculateBorder(
                textRect, 0, 0);
        rect.top     += fm.top - fm.ascent;
        rect.bottom  += fm.bottom - fm.descent;
        fm.top = fm.ascent = Math.round(rect.top);
        fm.bottom = fm.descent = Math.round(rect.bottom);
    }

    @Override
    public Size measure(int maxHeight) {
        maxHeight = fitHeight ? maxHeight : -1;
        Size size = measureChildes(maxHeight);
        if (size.canResize) {
            maxHeight = size.height;
            size = measureChildes(maxHeight);
        }
        int border = Math.round(borderSize * 2);
        bWidth  = size.width  + hPadding * 2 + border;
        bHeight = size.height + vPadding * 2 + border;

        int width = bWidth + leftMargin + rightMargin;
        return mSize = Size.with(width, bHeight);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text
            , int start, int end, float x, int top
            , int y, int bottom, Paint paint) {
        //测量文本
        RectF rectF = textMeasure(text,
                start, end,
                paint);
        //计算边框的大小和位置
        RectF rect = caculateBorder(
                rectF,
                x, y);
        onDraw(canvas, rect);
    }

    @Override
    public void draw(Canvas canvas, RectF rectF) {
        RectF rect = caculateBorder(
                rectF, 0, 0);
        onDraw(canvas, rect);
    }

    private void onDraw(Canvas canvas, RectF rect) {
        //测试 渲染边框
        debugDraw(canvas, rect, 0, 0);
        //绘制背景
        drawBackground(canvas, rect);
        //测试 渲染边框
        debugDraw(canvas, rect, 0, 0);
        //绘制子元素
        drawChildes(canvas, rect);
        //测试 渲染边框
        debugDraw(canvas, rect, 0, 0);
    }

    protected void drawChildes(Canvas canvas, RectF rect) {
        float halfBorder = borderSize / 2f;
        rect.inset(halfBorder, halfBorder);
        rect.inset(hPadding, vPadding);
        float dx = rect.left;
        int saveCount = canvas.save();
        canvas.translate(dx, 0);
        onDrawChildes(canvas, rect);
        canvas.restoreToCount(saveCount);
    }

    protected void onDrawChildes(Canvas canvas, RectF rect) {
        float dx = 0;
        for (AbsSpan child : mChilds) {
            canvas.save();
            canvas.translate(dx, 0);
            child.draw(canvas, rect);
            canvas.restore();

            Size size = child.size();
            dx += size.width;
        }
    }

    private RectF drawBackground(Canvas canvas, RectF rect) {
        float halfBorder = borderSize / 2f;
        rect.inset(halfBorder, halfBorder);
        Path roundPath = obtainPath(rect);
        canvas.drawPath(roundPath, mPaint);
        return rect;
    }

    protected final Size measureChildes(int maxHeight) {
        mChilds.clear();
        int width = 0, height = 0;
        boolean canResize = false;
        for (Builder node : mNodes) {
            AbsSpan child = node.build();
            Size size = child.measure(maxHeight);
            if (height < size.height) {
                height = size.height;
            }
            canResize |= node.fitHeight;
            width += size.width;
            mChilds.add(child);
        }
        return Size.with(width, height, canResize);
    }

    private RectF caculateBorder(RectF rectF, float x, float y) {
        rectF.offset(x, y);
        //计算边框大小
        RectF rect = new RectF();
        rect.right = bWidth;
        rect.bottom = bHeight;
        //计算偏移量
        float diffHeight = rectF.height() - bHeight;
        float offsetY = diffHeight * align;
        float dy = rectF.top + offsetY;
        float dx = x + leftMargin;
        rect.offset(dx, dy);
        return rect;
    }

    private Path obtainPath(RectF rectF) {
        Path path = new Path();
        path.addRoundRect(rectF,
                radius,
                Path.Direction.CW);
        path.close();
        return path;
    }

    @Override
    public Size size() {
        return mSize;
    }
}
