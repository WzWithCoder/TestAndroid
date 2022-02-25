package com.example.wangzheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class LimitHeightLayout extends FrameLayout {
    private int maxHeight = -1;

    public LimitHeightLayout(Context context) {
        super(context);
    }

    public LimitHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitHeightLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (maxHeight < heightSize) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
}