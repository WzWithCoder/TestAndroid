package com.example.wangzheng.widget.multi_input;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.fragment.app.Fragment;

public abstract class KeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener
        , ViewTreeObserver.OnWindowAttachListener {
    //防抖动屏障 分屏模式可适当减小
    private int debounceBarrier = 8;
    private int displayHeight = -1;
    private int lastHeight = -1;
    private View author = null;
    private Rect outRect = new Rect();

    public KeyboardListener(Activity context) {
        Window window = context.getWindow();
        author = window.getDecorView();
        ViewTreeObserver observer = author.getViewTreeObserver();
        observer.addOnWindowAttachListener(this);
    }

    public KeyboardListener(Fragment fragment) {
        author = fragment.getView();
        ViewTreeObserver observer = author.getViewTreeObserver();
        observer.addOnWindowAttachListener(this);
    }

    public KeyboardListener(View view) {
        this.author = view;
        ViewTreeObserver observer = author.getViewTreeObserver();
        observer.addOnWindowAttachListener(this);
    }

    @Override
    public final void onWindowAttached() {
        ViewTreeObserver observer = author.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(this);
    }

    @Override
    public final void onGlobalLayout() {
        author.getWindowVisibleDisplayFrame(outRect);
        if (displayHeight <= 0) {
            displayHeight = outRect.bottom;
        }
        int offset = displayHeight - outRect.bottom;
        boolean isVisible = offset > debounceBarrier;
        if (offset != lastHeight) {
            onChangeVisible(offset, isVisible);
        }
        lastHeight = offset;
    }

    @Override
    public final void onWindowDetached() {
        ViewTreeObserver observer = author.getViewTreeObserver();
        observer.removeOnGlobalLayoutListener(this);
        observer.removeOnWindowAttachListener(this);
    }

    public abstract void onChangeVisible(int height, boolean isVisible);

    public static KeyboardListener on(Activity context, OnKeyboardListener listener) {
        return new KeyboardListener(context) {
            public void onChangeVisible(int height, boolean isVisible) {
                listener.onChangeVisible(height, isVisible);
            }
        };
    }

    public static KeyboardListener on(View context, OnKeyboardListener listener) {
        return new KeyboardListener(context) {
            public void onChangeVisible(int height, boolean isVisible) {
                listener.onChangeVisible(height, isVisible);
            }
        };
    }

    public static interface OnKeyboardListener {
        void onChangeVisible(int height, boolean isVisible);
    }
}