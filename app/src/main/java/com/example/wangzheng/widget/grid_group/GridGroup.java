package com.example.wangzheng.widget.grid_group;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 网格GridGroup
 * Created by wangzheng on 2019/7/1.
 */
public class GridGroup extends ViewGroup {

    GridLayoutManager mLayoutManager;

    public GridGroup(Context context) {
        this(context, null);
    }

    public GridGroup(Context context, AttributeSet attrs) {
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