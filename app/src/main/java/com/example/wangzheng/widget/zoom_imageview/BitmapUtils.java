package com.example.wangzheng.widget.zoom_imageview;

/**
 * Create by wangzheng on 2018/11/19
 */
public class BitmapUtils {
    public static int computeSampleSize(int size) {
        int roundedSize;
        if (size <= 8) {
            roundedSize = 1;
            while (roundedSize < size) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (size + 7) / 8 * 8;
        }
        return roundedSize;
    }
}
