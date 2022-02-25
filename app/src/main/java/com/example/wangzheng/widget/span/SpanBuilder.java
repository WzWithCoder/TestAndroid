package com.example.wangzheng.widget.span;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

public class SpanBuilder extends SpannableStringBuilder {

    public static SpanBuilder from(CharSequence text) {
        return new SpanBuilder().append(text);
    }

    public static SpanBuilder from(CharSequence text, Object... spans) {
        return new SpanBuilder().append(text, spans);
    }

    @Override
    public SpanBuilder append(CharSequence text) {
        super.append(text);
        return this;
    }

    public SpanBuilder append(CharSequence text, Object... spans) {
        int start = length();
        super.append(text);
        int end = length();
        apply(start, end, spans);
        return this;
    }

    private void apply(int start, int end, Object... spans) {
        if (spans == null) return;
        for (Object span : spans) {
            setSpan(span, start, end);
        }
    }

    private void setSpan(Object span, int start, int end) {
        super.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static SpannableString setSpans(CharSequence text, Object... spans) {
        SpannableString spannable = new SpannableString(text);
        for (Object span : spans) {
            spannable.setSpan(span, 0, text.length()
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
}