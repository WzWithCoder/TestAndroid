package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.Rect;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

public class MarqueTextView extends AppCompatTextView {

    public MarqueTextView(Context context) {
        super(context);
        inflate();
    }

    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate();
    }

    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate();
    }

    private void inflate(){
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setMarqueeRepeatLimit(-1);
        setHorizontallyScrolling(true);
        setSingleLine();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public boolean isActivated() {
        return true;
    }
}