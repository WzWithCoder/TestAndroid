package com.example.wangzheng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * Created by wangzheng on 2016/8/24.
 */
public class FlexibleListView extends ListView {

    private static int mMaxOverDistance = 50;

    public FlexibleListView(Context context) {
        this(context, null);
    }

    public FlexibleListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public FlexibleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mMaxOverDistance = (int) (metrics.density * mMaxOverDistance);
    }


    @Override
    protected boolean overScrollBy(int deltaX, int deltaY,
                                   int scrollX, int scrollY,
                                   int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY,
                                   boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY,
                scrollX, scrollY,
                scrollRangeX, scrollRangeY,
                maxOverScrollX, mMaxOverDistance, isTouchEvent);
    }
}