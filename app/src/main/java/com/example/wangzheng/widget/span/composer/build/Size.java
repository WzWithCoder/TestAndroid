package com.example.wangzheng.widget.span.composer.build;

/**
 * Create by wangzheng on 2021/5/10
 */
public final class Size {
    public int width;
    public int height;
    public boolean canResize;

    public Size(int width, int height) {
        this.width = Math.max(width, 0);
        this.height = Math.max(height, 0);
    }

    public static Size with(int width, int height) {
        return new Size(width, height);
    }

    public Size(int width, int height, boolean canResize) {
        this.width = Math.max(width, 0);
        this.height = Math.max(height, 0);
        this.canResize = canResize;
    }

    public static Size with(int width, int height, boolean canResize) {
        return new Size(width, height, canResize);
    }

    public static Size empty() {
        return new Size(0,0);
    }
}
