package com.example.wangzheng.widget.round;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Create by wangzheng on 2018/5/8
 */
public class RoundFrameLayout extends FrameLayout {
    private RoundManager mRoundManager;

    public RoundFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRoundManager = new RoundManager(this, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRoundManager.onMeasure(cellSpec-> {
            setMeasuredDimension(cellSpec, cellSpec);
        });
    }

    @Override
    public void draw(Canvas canvas) {
        mRoundManager.draw(canvas, c-> {
            super.draw(c);
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mRoundManager.dispatchDraw(canvas, c-> {
            super.dispatchDraw(c);
        });
    }

    public void setRadius(int radius) {
        mRoundManager.setRadius(radius);
    }
}
