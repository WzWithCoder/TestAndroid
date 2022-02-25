package com.example.wangzheng.widget.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Create by wangzheng on 4/22/21
 */
public abstract class FixSpanClickable extends ClickableSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(@NonNull View widget) {
        widget.post(()-> click(widget));
    }

    public abstract void click(View v);
}
