package com.example.wangzheng.common;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Create by wangzheng on 2018/7/26
 */
public class DateKit {
    public static String DEFAULT_DATE_PATTERN = "yyyy.MM.dd HH:mm";

    public static String format(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(time);
    }

    public static String formatGmt8(long time, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return sdf.format(time);
    }

    public static String format(long time, String pattern, TimeZone zone) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(zone);
        return sdf.format(time);
    }
}
