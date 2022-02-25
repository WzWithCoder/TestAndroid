package com.ymt.server;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wangzheng on 2016/9/5.
 */

public class StreamToolKit {
    public static String readLine(InputStream is) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        int c1 = 0;
        int c2 = 0;
        while (c2 != -1 && !(c1 == '\r' && c2 == '\n')) {
            c1 = c2;
            c2 = is.read();
            stringBuffer.append((char) c2);
        }
        return stringBuffer.length() == 0 ? null : stringBuffer.toString();
    }

    public static String readString(InputStream is) {
        byte[] buffer = new byte[0];
        try {
            int size = is.available();
            buffer = new byte[size];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }

    public static String clearCRLF(String string) {
        return string.replaceAll("\r\n", "");
    }
}
