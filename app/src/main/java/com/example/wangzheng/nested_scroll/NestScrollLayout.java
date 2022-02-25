package com.example.wangzheng.nested_scroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.example.wangzheng.R;

/**
 * Create by wangzheng on 2018/5/9
 */
public class NestScrollLayout extends LinearLayout implements NestedScrollParent {
    private int   mTouchSlop;
    private float mLastX, mLastY;
    private float mDownX, mDownY;

    private int  mHeadHeight;
    private int  mHeadViewId;
    private View mHeadView;
    private int  mContentViewId;
    private View mContentView;

    public NestScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.NestScrollLayout, defStyleAttr, 0);
        mHeadViewId = typedArray.getResourceId(R.styleable.NestScrollLayout_head_id, -1);
        mContentViewId = typedArray.getResourceId(R.styleable.NestScrollLayout_content_id, -1);
        typedArray.recycle();

        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeadView = findViewById(mHeadViewId);
        mContentView = findViewById(mContentViewId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeadHeight = mHeadView.getMeasuredHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        boolean handle = false;
        View childView = NestedScrollKit.findChildViewUnder(this, x, y);
        if (NestedScrollKit.canChildScroll(childView)) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = mLastX = x;
                mDownY = mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = mDownX - x;
                float dy = mDownY - y;
                if (handle = Math.abs(dy) > mTouchSlop &&
                        canInterceptTouchEvent(dx, dy)) {
                    Log.e(getClass().getSimpleName(), "InterceptTouchEvent");
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastX = x;
        mLastY = y;
        return handle;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        boolean handle = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                //触摸到空白出,TextView不能点击,拦截DOWN事件
                View child = NestedScrollKit.findChildViewUnder(this, x, y);
                handle = child == null || !child.isClickable();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = mLastX - x;
                float dy = mLastY - y;
                if (handle = canInterceptTouchEvent(dx, dy)) {
                    scrollY(dy);
                } else {
                    event.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                    event.setAction(action);
                    Log.e(getClass().getSimpleName(), "user action dispatchTouchEvent");
                }
                break;
        }
        mLastX = x;
        mLastY = y;
        return handle;
    }

    @Override
    public boolean onNestedScroll(int dx, int dy, int[] consumed) {
        consumed[1] = (int) scrollY(dy);
        return dy > 0 && consumed[1] > 0;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        Log.e(getClass().getSimpleName(), "requestDisallowInterceptTouchEvent");
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    private boolean canInterceptTouchEvent(float dx, float dy) {
        int orientation = NestedScrollKit.getOrientation(dx, dy);
        return (orientation == 'u' && getScrollY() < mHeadHeight) ||
                (orientation == 'd' && getScrollY() > 0);
    }

    private float scrollY(float offsetY) {
        float scrollY = getScrollY() + offsetY;
        if (scrollY > mHeadHeight) {
            offsetY -= (scrollY - mHeadHeight);
        } else if (scrollY < 0) {
            offsetY -= (scrollY);
        }
        scrollBy(0, (int) offsetY);
        return offsetY;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        layoutChild(mContentView, mHeadHeight);
    }

    private void layoutChild(View child, int offsetY) {
        final Rect out = new Rect();
        out.set(child.getLeft(),
                child.getTop(),
                child.getRight(),
                child.getBottom() + offsetY);
        child.layout(out.left, out.top, out.right, out.bottom);
    }

    //https://blog.csdn.net/dreamsever/article/details/53907691
}
