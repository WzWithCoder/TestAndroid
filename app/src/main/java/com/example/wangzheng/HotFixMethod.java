package com.example.wangzheng;

import android.util.Log;

import com.example.wangzheng.ui.MainActivity;

import java.lang.reflect.Method;

/**
 * https://github.com/alibaba/AndFix/tree/master/src/com/alipay/euler/andfix
 * https://www.jianshu.com/p/e81b01561e1f
 * Create by wangzheng on 2021/7/20
 */
public final class HotFixMethod {

    public static void test() {
        try {
            final Class clazz1 = MainActivity.class;
            Method newMethod = clazz1.getMethod("newMethod");
            final Class clazz2 = HotFixMethod.class;
            Method oldMethod = clazz2.getMethod("oldMethod");
            HotFixMethod.replace(newMethod, oldMethod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        HotFixMethod.oldMethod();
    }

    public static void oldMethod() {
        Log.e("hot-fix","oldMethod");
    }

    /**
     * 方法替换
     * @param newMethod
     * @param oldMethod
     */
    public static native void replace(Method newMethod, Method oldMethod);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static final class NativeMethodSize {
        public static void method1(){}
        public static void method2(){}
    }
}
