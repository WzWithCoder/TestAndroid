package com.example.wangzheng.widget.span.composer.build;

import com.example.wangzheng.widget.span.composer.SpanComposer;

/**
 * Create by wangzheng on 2021/5/14
 */
public class CompBuilder extends Builder<SpanComposer> {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public int orientation = HORIZONTAL;

    public CompBuilder orientation(int orientation) {
        this.orientation = orientation;
        return this;
    }

    @Override
    public SpanComposer build() {
        return new SpanComposer(this);
    }
}
