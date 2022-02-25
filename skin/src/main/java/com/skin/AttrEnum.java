package com.skin;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

/**
 * Created by wangzheng on 2017/12/20.
 */

public enum AttrEnum {
    background() {
        @Override
        void apply(View view, SkinAttr attr) {
            if (color.equals(attr.resType)) {
                view.setBackgroundColor(SkinManager.getInstance().getColor(attr));
            } else if (drawable.equals(attr.resType)) {
                Drawable bg = SkinManager.getInstance().getDrawable(attr);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(bg);
                } else {
                    view.setBackgroundDrawable(bg);
                }
            }
        }
    }, textColor() {
        @Override
        void apply(View view, SkinAttr attr) {
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (color.equals(attr.resType)) {
                    textView.setTextColor(SkinManager.getInstance().colorStateList(attr));
                }
            }
        }
    };

    public static boolean contain(String name) {
        for (AttrEnum attrEnum : values()) {
            if (attrEnum.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    String color = "color";
    String drawable = "drawable";

    abstract void apply(View view, SkinAttr attr);
}
