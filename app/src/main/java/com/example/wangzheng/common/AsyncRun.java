package com.example.wangzheng.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by wangzheng on 2017/6/13.
 */

public class AsyncRun {
    public static void run(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void runOnUiThread(Runnable runnable) {
        Handler h = new Handler(Looper.getMainLooper());
        h.postAtFrontOfQueue(runnable);
    }
}
