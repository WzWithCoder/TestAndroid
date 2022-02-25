package com.example.wangzheng.widget.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;

import java.util.Arrays;

/**
 * Create by wangzheng on 12/1/16
 */
public final class RoundManager {
    private float[] radius = new float[8];

    private int borderColor;
    private int borderWidth;
    private int layoutShape;

    private boolean canClipBground;
    private boolean isForeBorder;

    private Paint mBorderPaint;
    private Paint mRoundPaint;
    private Paint mLayerPaint;

    private View anchor;

    public RoundManager(View anchor, AttributeSet attrs) {
        this.anchor = anchor;
        Context context = anchor.getContext();

        TypedArray attributes = context.obtainStyledAttributes(
                attrs, R.styleable.RoundLayout);
        canClipBground = attributes.getBoolean(
                R.styleable.RoundLayout_clip_background, true);
        isForeBorder = attributes.getBoolean(
                R.styleable.RoundLayout_border_fore, true);
        borderColor = attributes.getColor(
                R.styleable.RoundLayout_border_color, -1);
        borderWidth = attributes.getDimensionPixelSize(
                R.styleable.RoundLayout_border_width, 0);
        layoutShape = attributes.getInt(
                R.styleable.RoundLayout_shape, -1);

        inflateRadius(attributes);

        attributes.recycle();

        buildPaints(context);
    }

    private void inflateRadius(TypedArray attributes) {
        int index = R.styleable.RoundLayout_radius;
        if (attributes.hasValue(index)) {
            int r = attributes.getDimensionPixelSize(index, 0);
            Arrays.fill(radius, r);
        }
        index = R.styleable.RoundLayout_left_top_radius;
        if (attributes.hasValue(index)) {
            radius[0] = radius[1] = attributes
                    .getDimensionPixelSize(index, 0);
        }
        index = R.styleable.RoundLayout_right_top_radius;
        if (attributes.hasValue(index)) {
            radius[2] = radius[3] = attributes
                    .getDimensionPixelSize(index, 0);
        }
        index = R.styleable.RoundLayout_right_bottom_radius;
        if (attributes.hasValue(index)) {
            radius[4] = radius[5] = attributes
                    .getDimensionPixelSize(index, 0);
        }
        index = R.styleable.RoundLayout_left_bottom_radius;
        if (attributes.hasValue(index)) {
            radius[6] = radius[7] = attributes
                    .getDimensionPixelSize(index, 0);
        }
    }

    private void buildPaints(Context context) {
        mLayerPaint = new Paint();

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2*borderWidth);
        mBorderPaint.setColor(borderColor);

        PorterDuff.Mode mode = isForeBorder ?
                PorterDuff.Mode.SRC_OVER :
                PorterDuff.Mode.DST_OVER;
        PorterDuffXfermode xfermode =
                new PorterDuffXfermode(mode);
        mBorderPaint.setXfermode(xfermode);

        mRoundPaint = new Paint();
        mRoundPaint.setAntiAlias(true);
        int color = CommonKit.colorAccent(context);
        mRoundPaint.setColor(color);
        mRoundPaint.setStyle(Paint.Style.FILL);

        boolean isOMR1 = Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O_MR1;
        mode = isOMR1 ? PorterDuff.Mode.DST_IN :
                PorterDuff.Mode.DST_OUT;
        xfermode = new PorterDuffXfermode(mode);
        mRoundPaint.setXfermode(xfermode);
    }

    public void onMeasure(Handle<Integer> setMeasuredDimension) {
        int width  = anchor.getMeasuredWidth();
        int height = anchor.getMeasuredHeight();
        if (layoutShape == 1) {
            int size = Math.max(width, height);
            setMeasuredDimension.invoke(size);
            Arrays.fill(radius, size / 2f);
        } else if (layoutShape == 2) {
            Arrays.fill(radius, height / 2f);
            if (height > 0 && height > width) {
              anchor.setMinimumWidth(height);
            }
        }
    }

    public void draw(Canvas canvas, Handle<Canvas> onDraw) {
        if (canClipBground) {
            int saveCount = saveLayer(canvas);
            onDraw.invoke(canvas);
            clipRoundBounds(canvas);
            canvas.restoreToCount(saveCount);
        } else {
            onDraw.invoke(canvas);
        }
    }

    public void dispatchDraw(Canvas canvas, Handle<Canvas> dispatchDraw) {
        int saveCount = saveLayer(canvas);
        dispatchDraw.invoke(canvas);
        drawBorder(canvas);
        clipRoundBounds(canvas);
        canvas.restoreToCount(saveCount);
    }

    private int saveLayer(Canvas canvas) {
        final int offset = 0;
        RectF bounds = getClipBounds(offset);
        int saveFlags = Canvas.ALL_SAVE_FLAG;
        int saveCount = canvas.saveLayer(
                bounds, mLayerPaint,
                saveFlags);
        return saveCount;
    }

    private void drawBorder(Canvas canvas) {
        if (borderColor == -1) return;
        final float offset = 0/*borderWidth / 2f*/;
        Path path = getRoundPath(radius, offset);
        canvas.drawPath(path, mBorderPaint);
    }

    private void clipRoundBounds(Canvas canvas) {
        Path path = getPath(radius, 0);
        canvas.drawPath(path, mRoundPaint);
    }

    private Path getPath(float[] radius, float offset) {
        Path roundPath = getRoundPath(radius, offset);
        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O_MR1) {
            return roundPath;
        }
        Path boundPath = getBoundPath(offset);
        boundPath.op(roundPath, Path.Op.DIFFERENCE);
        return boundPath;
    }

    private Path getRoundPath(float[] radius, float offset) {
        RectF bounds = getClipBounds(offset);
        Path path = new Path();
        path.addRoundRect(bounds, radius, Path.Direction.CW);
        return path;
    }

    private Path getBoundPath(float offset) {
        RectF bounds = getClipBounds(offset);
        Path path = new Path();
        path.addRect(bounds, Path.Direction.CW);
        return path;
    }

    private RectF getClipBounds(float offset) {
        final int start  = 0;
        int height = anchor.getHeight();
        int width  = anchor.getWidth();
        RectF bounds = new RectF(start
                , start, width, height);
        bounds.inset(offset, offset);
        return bounds;
    }

    public void setRadius(int r) {
        r = CommonKit.dip2px(r);
        Arrays.fill(radius, r);
        anchor.postInvalidate();
    }

    public void setRadius(int leftTop,  int leftBottom,
                          int rightTop, int rightBottom) {
        radius[0] = radius[1] = CommonKit.dip2px(leftTop);
        radius[2] = radius[3] = CommonKit.dip2px(rightTop);
        radius[4] = radius[5] = CommonKit.dip2px(rightBottom);
        radius[6] = radius[7] = CommonKit.dip2px(leftBottom);
        anchor.postInvalidate();
    }

    public void setBorderColor(int color) {
        mBorderPaint.setColor(color);
        anchor.postInvalidate();
    }

    public void setBorderWidth(int width) {
        width = 2 * CommonKit.dip2px(width);
        mBorderPaint.setStrokeWidth(width);
        anchor.postInvalidate();
    }

    public void setShape(int shape) {
        this.layoutShape = shape;
        anchor.postInvalidate();
    }

    public interface Handle<T> {
        void invoke(T t);
    }
}
