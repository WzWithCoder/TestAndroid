package com.example.wangzheng.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class BounceView extends LinearLayout {
    private View mContentView;
    private Scroller mScroller;
    private GestureDetector mGestureDetector;

    public BounceView(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public BounceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                int dis = (int) ((distanceY - 0.5) / 2);
                smoothScrollBy(0, dis);
                return false;
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mContentView = getChildAt(0);
        }
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isInterceptTouchEvent()) return true;
        return super.onInterceptTouchEvent(ev);
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInterceptTouchEvent())
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    smoothScrollTo(0, 0);
                    break;
                default:
                    return mGestureDetector.onTouchEvent(event);
            }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        //判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    private boolean isInterceptTouchEvent() {
        return canPullDown() || canPullUp();
    }

    private boolean canPullDown() {
        return getScrollY() == 0
                || mContentView.getHeight() < getHeight() + getScrollY();
    }

    private boolean canPullUp() {
        return mContentView.getHeight() <= getHeight() + getScrollY();
    }
}