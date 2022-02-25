package com.example.wangzheng.nested_scroll;

import android.content.Context;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class NestedListView extends ListView implements NestedScrollChild {

    private int mScrollPointerId;
    private int mLastTouchX;
    private int mLastTouchY;

    public NestedListView(Context context) {
        super(context);
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final MotionEvent vtev = MotionEvent.obtain(e);
        final int action = e.getAction();
        final int actionIndex = e.getActionIndex();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = e.getPointerId(actionIndex);
                mLastTouchX = (int) (e.getX() + 0.5f);
                mLastTouchY = (int) (e.getY() + 0.5f);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mScrollPointerId = e.getPointerId(actionIndex);
                mLastTouchX = (int) (e.getX(actionIndex) + 0.5f);
                mLastTouchY = (int) (e.getY(actionIndex) + 0.5f);
                break;
            case MotionEvent.ACTION_MOVE:
                final int index = e.findPointerIndex(mScrollPointerId);
                if (index < 0) {
                    return false;
                }
                final int x = (int) (e.getX(index) + 0.5f);
                final int y = (int) (e.getY(index) + 0.5f);
                int dx = mLastTouchX - x;
                int dy = mLastTouchY - y;
                int[] scrollConsumed = new int[2];
                NestedScrollParent nestedParent = (NestedScrollParent) getParent();
                if (nestedParent.onNestedScroll(dx, dy, scrollConsumed)) {
                    vtev.offsetLocation(scrollConsumed[0], scrollConsumed[1]);
                }
                mLastTouchX = x + scrollConsumed[0];
                mLastTouchY = y + scrollConsumed[1];
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean canScroll() {
        return false;
    }
}
