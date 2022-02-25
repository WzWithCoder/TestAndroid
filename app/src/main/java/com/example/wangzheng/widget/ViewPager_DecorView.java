package com.example.wangzheng.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Create by wangzheng on 4/20/21
 */
@ViewPager.DecorView
public class ViewPager_DecorView extends androidx.appcompat.widget.AppCompatTextView {
    public ViewPager_DecorView(Context context) {
        super(context);
    }

    public ViewPager_DecorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPager_DecorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
