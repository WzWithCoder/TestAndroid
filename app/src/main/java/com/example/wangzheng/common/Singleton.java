package com.example.wangzheng.common;

//单例辅助
public abstract class Singleton<T> {
    private volatile T mInstance;

    protected abstract T create();

    //双检查锁
    public final T get() {
        if (mInstance == null) {
            synchronized (this) {
                if (mInstance == null) {
                    mInstance = create();
                }
            }
        }
        return mInstance;
    }
}