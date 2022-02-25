package com.example.wangzheng.widget.round;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * Create by wangzheng on 12/1/20
 */
public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {
    private RoundManager mRoundManager;
    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context
            , @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public void setShape(int shape) {
        mRoundManager.setShape(shape);
    }
}
