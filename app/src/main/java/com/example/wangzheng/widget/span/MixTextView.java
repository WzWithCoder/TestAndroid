package com.example.wangzheng.widget.span;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.example.wangzheng.widget.span.composer.AbsSpan;
import com.example.wangzheng.widget.span.composer.LayoutSpan;
import com.example.wangzheng.widget.span.composer.SpanComposer;
import com.example.wangzheng.widget.span.composer.build.Builder;
import com.example.wangzheng.widget.span.composer.build.CompBuilder;
import com.example.wangzheng.widget.tv_fold.FoldTextView;

/**
 * Create by wangzheng on 2021/6/16
 */
public class MixTextView extends FrameLayout {
    SpanBuilder mBuilder = new SpanBuilder();

    public MixTextView(Context context) {
        this(context, null);
    }

    public MixTextView(Context context
            , @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MixTextView(Context context
            , @Nullable AttributeSet attrs
            , int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MixTextView append(CharSequence text, Object... spans) {
        mBuilder.append(text, spans);
        return this;
    }

    public MixTextView append(CharSequence text, View view) {
        Builder layout = new CompBuilder();
        mBuilder.append(text, new Builder() {
            public AbsSpan build() {
                return new LayoutSpan(layout, view);
            }
        }.build());

        removeView(view);
        addView(view);
        return this;
    }

    public void build() {
        FoldTextView textView = new FoldTextView(getContext());
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        removeView(textView);
        addView(textView);
    }
}
