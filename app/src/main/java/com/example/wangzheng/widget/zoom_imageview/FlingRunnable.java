package com.example.wangzheng.widget.zoom_imageview;

import android.graphics.RectF;
import android.view.View;
import android.widget.OverScroller;

public abstract class FlingRunnable implements Runnable {
    private View mContext;
    private int  mCurrentX, mCurrentY;
    private final OverScroller mScroller;

    public FlingRunnable(View context) {
        mContext = context;
        mScroller = new OverScroller(context.getContext());
    }

    public void cancelFling() {
        mScroller.forceFinished(true);
    }

    public void fling(int viewWidth, int viewHeight, int velocityX, int velocityY, RectF rect) {
        if (null == rect) {
            return;
        }

        final int startX = Math.round(-rect.left);
        final int minX, maxX, minY, maxY;

        if (viewWidth < rect.width()) {
            minX = 0;
            maxX = Math.round(rect.width() - viewWidth);
        } else {
            minX = maxX = startX;
        }

        final int startY = Math.round(-rect.top);
        if (viewHeight < rect.height()) {
            minY = 0;
            maxY = Math.round(rect.height() - viewHeight);
        } else {
            minY = maxY = startY;
        }

        mCurrentX = startX;
        mCurrentY = startY;

        // If we actually can move, fling the scroller
        if (startX != maxX || startY != maxY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
            mContext.post(this);
        }
    }

    @Override
    public void run() {
        if (mScroller.isFinished()) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            final int newX = mScroller.getCurrX();
            final int newY = mScroller.getCurrY();

            apply(mCurrentX - newX, mCurrentY - newY);

            mCurrentX = newX;
            mCurrentY = newY;

            mContext.post(this);
        }
    }

    public abstract void apply(float dx, float dy);
}