package com.example.wangzheng.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create by wangzheng on 2019/8/22
 */
public class IoKit {
    public static void close(InputStream is) {
        if (is == null) return;
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(OutputStream os) {
        if (os == null) return;
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
