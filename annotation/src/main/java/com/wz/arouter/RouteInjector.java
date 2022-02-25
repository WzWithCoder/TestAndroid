package com.wz.arouter;

/**
 * Create by wangzheng on 2018/6/12
 */
public class RouteInjector {
    public static boolean route(String path,Call call) {
        return true;
    }

    public static interface Call {
        void call(String uri);
    }
}
