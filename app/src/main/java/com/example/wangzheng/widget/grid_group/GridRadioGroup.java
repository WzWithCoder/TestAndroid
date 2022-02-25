package com.example.wangzheng.widget.grid_group;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RadioGroup;

/**
 * 网格GridRadioGroup
 * Created by wangzheng on 2019/7/1.
 */
public class GridRadioGroup extends RadioGroup {

    GridLayoutManager mLayoutManager;

    public GridRadioGroup(Context context) {
        this(context, null);
    }

    public GridRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLayoutManager = new GridLayoutManager(this, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        int[] size = mLayoutManager.onMeasure(
                widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(size[0], size[1]);
    }

    @Override
    protected void onLayout(boolean changed,
                            int l, int t, int r, int b) {
        mLayoutManager.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLayoutManager.onDraw(canvas);
    }
}