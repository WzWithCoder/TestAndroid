package com.example.wangzheng.widget.round;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * Create by wangzheng on 12/1/20
 */
public class BubbleTextView extends androidx.appcompat.widget.AppCompatTextView {
    private RoundManager mRoundManager;
    public BubbleTextView(Context context) {
        this(context, null);
    }

    public BubbleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context
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

    public void setRadius(int r) {
        mRoundManager.setRadius(r);
    }
}
