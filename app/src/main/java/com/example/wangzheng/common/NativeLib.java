package com.example.wangzheng.common;

import android.graphics.SurfaceTexture;
import android.view.Surface;

/**
 * Create by wangzheng on 2018/3/26
 */

public class NativeLib {
    static {
        //region FFMPEG https://github.com/dxjia/ffmpeg-compile-shared-library-for-android
//        System.loadLibrary("avutil-54");
//        System.loadLibrary("avcodec-56");
//        System.loadLibrary("avformat-56");
//        System.loadLibrary("avdevice-56");
//        System.loadLibrary("swresample-1");
//        System.loadLibrary("swscale-3");
//        System.loadLibrary("avfilter-5");
//        System.loadLibrary("postproc-53");
//        //endregion
//        //region YUV https://github.com/illuspas/libyuv-android
//        System.loadLibrary("yuv");
//        //endregion
//        System.loadLibrary("native-lib");
    }

    public static native String callHello(String str);

    public static native String callOnLoad(String str);

    public static String fromJniCallback(String str) {
        return str + " return ok";
    }

    public static native void setDataSource(String uri);

    //解决 cannot locate symbol "ANativeWindow_fromSurfaceTexture"
    public static void createSurface(String uri, SurfaceTexture surfaceTexture) {
        createSurface(uri, new Surface(surfaceTexture));
    }

    public static native void createSurface(String uri, Surface surface);

    public static native void destorySurface(String uri);

    public static native void play(String uri);

    public static native void pause(String uri);

    public static native void release(String uri);

}
