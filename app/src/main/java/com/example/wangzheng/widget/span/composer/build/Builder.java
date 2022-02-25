package com.example.wangzheng.widget.span.composer.build;

import android.graphics.Paint;

import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.widget.span.composer.AbsSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by wangzheng on 2021/5/14
 */
public abstract class Builder <T extends AbsSpan>{

    public int size = 12;
    public float borderSize;
    public float align = 0.5f;
    public int bgColor;
    public Paint.Style style = Paint.Style.STROKE;
    public boolean fitHeight;
    public int vPadding, hPadding;
    public int leftMargin, rightMargin,
            topMargin, bottomMargin;
    public float[] radius = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

    public Builder size(int textSize) {
        this.size = CommonKit.sp2px(textSize);
        return this;
    }

    public Builder bordeSize(float size) {
        this.borderSize = CommonKit.dip2px(size);
        return this;
    }

    public Builder bgColor(int color) {
        this.bgColor = color;
        return this;
    }

    public Builder style(Paint.Style style) {
        this.style = style;
        return this;
    }

    public Builder align(float align) {
        this.align = align;
        return this;
    }

    public Builder fitHeight() {
        this.fitHeight = true;
        return this;
    }

    public Builder hMargin(int leftMargin, int rightMargin) {
        this.leftMargin = CommonKit.dip2px(leftMargin);
        this.rightMargin = CommonKit.dip2px(rightMargin);
        return this;
    }

    public Builder hMargin(int margin) {
        this.leftMargin = CommonKit.dip2px(margin);
        this.rightMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder vMargin(int topMargin, int bottomMargin) {
        this.topMargin = CommonKit.dip2px(topMargin);
        this.bottomMargin = CommonKit.dip2px(bottomMargin);
        return this;
    }

    public Builder vMargin(int margin) {
        this.topMargin = CommonKit.dip2px(margin);
        this.bottomMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder margin(int margin) {
        this.leftMargin = CommonKit.dip2px(margin);
        this.rightMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder leftMargin(int margin) {
        this.leftMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder rightMargin(int margin) {
        this.rightMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder topMargin(int margin) {
        this.topMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder bottomMargin(int margin) {
        this.bottomMargin = CommonKit.dip2px(margin);
        return this;
    }

    public Builder vPadding(int padding) {
        this.vPadding = CommonKit.dip2px(padding);
        return this;
    }

    public Builder hPadding(int padding) {
        this.hPadding = CommonKit.dip2px(padding);
        return this;
    }

    public Builder padding(int padding) {
        this.hPadding = CommonKit.dip2px(padding);
        this.vPadding = CommonKit.dip2px(padding);
        return this;
    }

    public Builder padding(int hPadding, int vPadding) {
        this.hPadding = CommonKit.dip2px(hPadding);
        this.vPadding = CommonKit.dip2px(vPadding);
        return this;
    }

    public Builder radius(int corner) {
        Arrays.fill(radius, CommonKit.dip2px(corner));
        return this;
    }

    public Builder radius(float leftRadius, float rightRadius){
        radius[0] = radius[1] = radius[6] = radius[7] = CommonKit.dip2px(leftRadius);
        radius[2] = radius[3] = radius[4] = radius[5] = CommonKit.dip2px(rightRadius);
        return this;
    }

    public Builder radius(float leftTop,  float leftBottom,
                          float rightTop, float rightBottom) {
        radius[0] = radius[1] =  CommonKit.dip2px(leftTop);
        radius[2] = radius[3] =  CommonKit.dip2px(rightTop);
        radius[4] = radius[5] =  CommonKit.dip2px(rightBottom);
        radius[6] = radius[7] =  CommonKit.dip2px(leftBottom);
        return this;
    }

    public Builder leftRadius(float r){
        radius[0] = radius[1] = radius[6] = radius[7] = CommonKit.dip2px(r);
        return this;
    }

    public Builder leftBottomRadius(float r){
        radius[6] = radius[7] = CommonKit.dip2px(r);
        return this;
    }

    public List<Builder> nodes = new ArrayList<>();
    public Builder join(Builder builder) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        nodes.add(builder);
        return this;
    }

    public abstract T build();
}