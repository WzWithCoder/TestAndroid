package com.example.wangzheng.widget.viewpager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.example.wangzheng.adapter.LoopPagerAdapter;
import com.example.wangzheng.common.Callable;

/**
 * Create by wangzheng on 2018/6/19
 */
public final class AutoScrollViewPager extends AutofitViewPager {
    private ChangePageWrapper mChangePageWrapper;

    private final long INTERVAL_TIME = 1 * 5000;
    private final int  what          = 0x34;

    private long delayMillis = INTERVAL_TIME;
    private int  lastIndex   = -1;

    private boolean isRunning = false;
    private boolean isStarted = false;

    private Message mMessage;

    protected final Handler mHandler = new Handler(msg -> {
        int current = getCurrentItem() + 1;
        PagerAdapter adapter = getAdapter();
        if (current >= adapter.getCount()) {
            current = 0;
        }
        final boolean smoothScroll = true;
        setCurrentItem(current, smoothScroll);
        sendMessageDelayed(INTERVAL_TIME);
        return true;
    });

    public AutoScrollViewPager(Context context) {
        this(context, null);
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean bool = super.onTouchEvent(ev);
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastIndex = getCurrentItem();
            updateRunning(false);
        } else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_CANCEL) {
            checkPageChange();
            updateRunning(true);
        }
        return bool;
    }

    @Override
    protected void onVisibilityChanged(View view, int visibility) {
        super.onVisibilityChanged(view, visibility);
        boolean now = visibility == VISIBLE;
        updateRunning(now);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateRunning(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        updateRunning(false);
    }

    private final void checkPageChange() {
        int index = getCurrentItem();
        if (index != lastIndex) {
            delayMillis = INTERVAL_TIME;
        }
    }

    protected void updateRunning(boolean now) {
        if (isRunning = isStarted && now) {
            sendMessageDelayed(delayMillis);
            delayMillis = INTERVAL_TIME;
        } else {
            delayMillis = fetchDelayTime();
            mHandler.removeMessages(what);
        }
    }

    private void sendMessageDelayed(long delayMillis) {
        mMessage = Message.obtain();
        mMessage.what = what;
        mHandler.removeMessages(what);
        mHandler.sendMessageDelayed(
                mMessage,
                delayMillis);
    }

    protected final long fetchDelayTime() {
        if (mMessage == null) return 0;
        long when = mMessage.getWhen();
        long millis = SystemClock.uptimeMillis();
        return when - millis;
    }

    public final void start() {
        this.isStarted = true;
        updateRunning(isStarted);
    }

    public final void stop() {
        this.isStarted = false;
        updateRunning(isStarted);
    }

    @Override
    public void addOnPageChangeListener(@NonNull OnPageChangeListener listener) {
        if (mChangePageWrapper == null) {
            mChangePageWrapper = new ChangePageWrapper(this);
            super.addOnPageChangeListener(mChangePageWrapper);
        }
        mChangePageWrapper.addOnPageChangeListener(listener);
    }

    public void addRealPageChangeListener(@NonNull OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
    }

    public void setAdapter(@Nullable PagerAdapter adapter, boolean isCircle) {
        if (isCircle) {
            LoopPagerAdapter.delegate(this, adapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    public void joinTimer(Callable<Long, Boolean> callable, long interval) {
        mHandler.postDelayed(new Runnable() {
            public final void run() {
                long timeMillis = fetchDelayTime();
                if (isRunning) {
                    callable.call(timeMillis);
                }
                mHandler.postDelayed(this, interval);
            }
        }, interval);
    }
}
