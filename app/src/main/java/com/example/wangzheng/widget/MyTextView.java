package com.example.wangzheng.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class MyTextView extends AppCompatEditText {

    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TextPaint paint = getPaint();
        LinearGradient linearGradient = new LinearGradient(
                0, 0, 100, 100,
                Color.RED, Color.YELLOW,
                LinearGradient.TileMode.MIRROR);
        paint.setShader(linearGradient);
    }
}