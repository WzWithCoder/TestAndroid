package com.example.wangzheng;

import android.util.Log;

import com.example.wangzheng.common.DateKit;
import com.example.wangzheng.common.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by wangzheng on 2016/4/22.
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static UncaughtExceptionHandler instance = null;

    private UncaughtExceptionHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public synchronized static UncaughtExceptionHandler apply() {
        if (instance == null) {
            instance = new UncaughtExceptionHandler();
        }
        return instance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        writeErrorLog(ex);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    public static void writeErrorLog(Throwable ex) {
        if (ex == null) return;
        Log.e("UncaughtException", "writeErrorLog: ", ex);
        String date = DateKit.format(System.currentTimeMillis());
        File file = new File(
                Storage.getExternalDir("crash_log"),
                date + "_error_log.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(fos);
            ex.printStackTrace(printWriter);
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
