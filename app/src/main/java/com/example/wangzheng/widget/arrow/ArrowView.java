package com.example.wangzheng.widget.arrow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.wangzheng.R;
import com.example.wangzheng.common.CommonKit;


/**
 * Create by wangzheng on 2018/12/21
 */
public class ArrowView extends View {

    private ArrowDrawable mArrowDrawable;

    public ArrowView(Context context) {
        this(context, null);
    }

    public ArrowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.ArrowView);
        float strokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.ArrowView_stroke_width, 10);
        int style = typedArray.getInt(
                R.styleable.ArrowView_style, 1);
        int orientation = typedArray.getInt(
                R.styleable.ArrowView_about, About.RIGHT);
        int color = typedArray.getColor(
                R.styleable.ArrowView_stroke_color,
                CommonKit.colorAccent(context));
        typedArray.recycle();

        mArrowDrawable = new ArrowDrawable()
                .color(color)
                .orientation(orientation)
                .strokeWidth(strokeWidth)
                .style(style == 1 ? Paint.Style.STROKE :
                        Paint.Style.FILL_AND_STROKE);

        mArrowDrawable.setCallback(this);

        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setOrientation((mArrowDrawable.orientation() + 1) % 4);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mArrowDrawable
                .width(getWidth())
                .height(getHeight())
                .draw(canvas);
    }

    public void setOrientation(int orientation) {
        if (getWidth() != getHeight() &&
                !isSameMode(orientation)) {
            swapSize();
        }
        mArrowDrawable.orientation(orientation);
        invalidate();
    }

    private boolean isSameMode(int orientation) {
        return (orientation <= 1 && mArrowDrawable.orientation() <= 1) ||
                (orientation > 1 && mArrowDrawable.orientation() > 1);
    }

    private void swapSize() {
        ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) getLayoutParams();
        layoutParams.width = getHeight();
        layoutParams.height = getWidth();
        requestLayout();
    }

    public int getOrientation() {
        return mArrowDrawable.orientation();
    }

    public interface About {
        int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    }
}
