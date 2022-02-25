package com.example.wangzheng;

import android.util.Log;

import com.example.wangzheng.common.DateKit;
import com.example.wangzheng.common.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Create by wangzheng on 2018/9/3
 */
public final class ExceptionProcessor {
    private final static String TAG = ExceptionProcessor.class.getSimpleName();

    public static void pushException(Throwable t) {
        Log.w(TAG, t);
        writeExLog(t);
    }

    public static void writeExLog(Throwable ex) {
        if (ex == null) return;
        String date = DateKit.format(System.currentTimeMillis());
        File file = new File(Storage.getExternalDir("ex_log"),
                date + "_ex_log.txt");
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
