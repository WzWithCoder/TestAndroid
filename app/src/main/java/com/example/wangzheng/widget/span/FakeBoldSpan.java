package com.example.wangzheng.widget.span;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

public class FakeBoldSpan extends CharacterStyle {

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setFakeBoldText(true);//一种伪粗体效果，比原字体加粗的效果弱一点
    }

}
