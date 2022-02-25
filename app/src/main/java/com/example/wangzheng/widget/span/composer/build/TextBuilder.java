package com.example.wangzheng.widget.span.composer.build;

import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.widget.span.composer.TextSpan;

public final class TextBuilder extends Builder<TextSpan> {
    public String text;
    public int textColor;
    public boolean fakeBold;
    public boolean strikeLine;
    public float strokeWidth;

    public TextBuilder text(String text) {
        this.text = text;
        return this;
    }

    public TextBuilder color(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TextBuilder fakeBold(boolean bold) {
        this.fakeBold = bold;
        return this;
    }

    public TextBuilder strokeWidth(float strokeWidth) {
        this.strokeWidth = CommonKit.dip2px(strokeWidth);
        return this;
    }

    public TextBuilder strikeLine() {
        this.strikeLine = true;
        return this;
    }

    public TextBuilder bordeSize(float size) {
        this.borderSize = CommonKit.dip2px(size);
        return this;
    }

    public TextBuilder bgColor(int color) {
        this.bgColor = color;
        return this;
    }

    public TextSpan build() {
        return new TextSpan(this);
    }
}