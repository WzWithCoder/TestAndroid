package com.example.wangzheng.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;

public class LineView extends View {

    private Path       mPath;
    private Paint      mPaint;
    private PathEffect mPathEffect;

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.LineView);
        int dashGap = typedArray.getDimensionPixelSize(
                R.styleable.LineView_dashGap, 0);
        int dashWidth = typedArray.getDimensionPixelSize(
                R.styleable.LineView_dashWidth, 0);
        int dashColor = typedArray.getColor(
                R.styleable.LineView_dashColor,
                CommonKit.colorAccent(context));
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(dashColor);

        mPathEffect = new DashPathEffect(new float[]{dashGap, dashWidth}, 0);
        mPaint.setPathEffect(mPathEffect);

        mPath = new Path();
    }

    public void setPathEffect(PathEffect pathEffect) {
        this.mPathEffect = pathEffect;
        mPaint.setPathEffect(mPathEffect);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float strokeWidth = Math.min(width, height);
        int offset = Math.round(strokeWidth / 2);
        mPath.reset();
        if (width > height) {//横线
            mPath.moveTo(0, offset);
            mPath.lineTo(width, offset);
        } else {//竖线
            mPath.moveTo(offset, 0);
            mPath.lineTo(offset, height);
        }
        mPaint.setStrokeWidth(strokeWidth);
        canvas.drawPath(mPath, mPaint);
    }
}