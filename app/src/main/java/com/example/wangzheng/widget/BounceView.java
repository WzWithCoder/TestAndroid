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
        //??????mScroller??????????????????
        if (mScroller.computeScrollOffset()) {
            //??????View???scrollTo()?????????????????????
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //????????????????????????????????????????????????????????????
            postInvalidate();
        }
        super.computeScroll();
    }

    //????????????????????????????????????
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //??????????????????????????????????????????
    public void smoothScrollBy(int dx, int dy) {
        //??????mScroller??????????????????
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//??????????????????invalidate()????????????computeScroll()?????????????????????????????????????????????????????????????????????
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