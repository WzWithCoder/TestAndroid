package com.example.wangzheng.common;

import static android.os.Message.obtain;

import android.os.Handler;
import android.os.Message;

/**
 * Create by wangzheng on 2021/11/19
 */
public final class Debounced {
    private int what = 0X666;
    private int when = 618;
    private Handler handler = new Handler();

    public Debounced() {}

    public Debounced(int when) {
        this.when = when;
    }

    public Debounced(int what, int when) {
        this.what = what;
        this.when = when;
    }

    public void post(Runnable runnable) {
        post(what, when, runnable);
    }

    public void post(int when, Runnable runnable) {
        post(what, when, runnable);
    }

    public void post(int what, int when, Runnable runnable) {
        handler.removeMessages(what);
        Message message = obtain(
                handler,
                runnable);
        message.what = what;
        handler.sendMessageDelayed(
                message,
                when);
    }

    public void clear() {
        handler.removeMessages(what);
    }
}
