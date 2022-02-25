package com.example.wangzheng.common;

/**
 * Create by wangzheng on 2018/7/4
 */
public class WrapMultiItem<T> {
    public int type;
    public int spanSise = -1;
    public boolean isCheck;
    public T data;

    public WrapMultiItem(T data, int type,int spanSise) {
        this.data = data;
        this.type = type;
        this.spanSise = spanSise;
    }

    public <T> T getData() {
        return (T) data;
    }
}
