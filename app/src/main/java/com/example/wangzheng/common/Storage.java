package com.example.wangzheng.common;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.example.wangzheng.App;

import java.io.File;

/**
 * Create by wangzheng on 2018/7/25
 */
public final class Storage {
    private static final String TAG = Storage.class.getSimpleName();

    /**
     * 获取应用专属缓存目录-随着app的删除而清空
     *
     * @param type 文件夹类型 -非必填
     * @return
     */
    public static File getCacheDir(final String type) {
        File fileDir = getExternalDir(type);

        if (fileDir == null) {
            fileDir = getInternalDir(type);
        }

        if (fileDir == null) {
            Log.e(TAG, "getCacheDir , unknown exception !");
        } else if (!fileDir.exists() && !fileDir.mkdirs()) {
            Log.e(TAG, "getCacheDir , make directory fail !");
        }
        return fileDir;
    }

    /**
     * 获取应用缓存目录
     *
     * @param dir 子目录
     * @return
     */
    public static File getStorageDirectory(final String dir) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.e(TAG, "getStorageDirectory , SD Card nonexistence or mount fail !");
            return null;
        }
        File fileDir = new File(Environment.getExternalStorageDirectory(),
                App.instance().getPackageName() + "/" + dir);

        if (!fileDir.exists() && !fileDir.mkdirs()) {
            Log.e(TAG, "getStorageDirectory , make directory fail !");
        }
        return fileDir;
    }

    /**
     * 获取SD卡缓存目录
     *
     * @param dir 文件夹类型-非必填
     *            {@link android.os.Environment#DIRECTORY_MUSIC},
     *            {@link android.os.Environment#DIRECTORY_PODCASTS},
     *            {@link android.os.Environment#DIRECTORY_RINGTONES},
     *            {@link android.os.Environment#DIRECTORY_ALARMS},
     *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *            {@link android.os.Environment#DIRECTORY_PICTURES},
     *            {@link android.os.Environment#DIRECTORY_MOVIES}.
     *            or 自定义文件夹名称
     * @return 缓存目录文件夹
     */
    public static File getExternalDir(final String dir) {
        Context context = App.instance();
        File fileDir = null;

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.e(TAG, "getExternalDir , SD Card nonexistence or mount fail !");
            return fileDir;
        }

        if (TextUtils.isEmpty(dir)) {
            //storage/emulated/0/Android/data/app_package_name/cache
            fileDir = context.getExternalCacheDir();
        } else {
            //storage/emulated/0/Android/data/app_package_name/files/dir
            fileDir = context.getExternalFilesDir(dir);
        }

        if (fileDir == null) {//有些手机需要通过自定义目录
            fileDir = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/" + context.getPackageName() + "/cache/" + dir);
        }

        if (!fileDir.exists() && !fileDir.mkdirs()) {
            Log.e(TAG, "getExternalDir , make directory fail !");
        }
        return fileDir;
    }

    /**
     * 获取内存缓存目录
     * 外部应用没有读写权限
     *
     * @param dir 子目录-非必填
     * @return 缓存目录文件夹
     */
    public static File getInternalDir(final String dir) {
        Context context = App.instance();
        File fileDir = null;

        if (TextUtils.isEmpty(dir)) {
            //data/data/app_package_name/cache
            fileDir = context.getCacheDir();
        } else {
            //data/data/app_package_name/files/dir
            fileDir = new File(context.getFilesDir(), dir);
        }

        if (!fileDir.exists() && !fileDir.mkdirs()) {
            Log.e(TAG, "getInternalDir , make directory fail !");
        }
        return fileDir;
    }

    /**
     * 清除缓存
     * @return
     */
    public static boolean clearCache() {
        if (!Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState())) {
            return false;
        }
        //storage/emulated/0/Android/data/app_package_name/cache
        File fileDir = App.instance().getExternalCacheDir();
        if (fileDir == null) {
            fileDir = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/" + App.instance().getPackageName() + "/cache");
        }
        return FileKit.delete(fileDir);
    }

    /**
     * 清除数据
     * @return
     */
    public static boolean clearData() {
        if (!Environment.MEDIA_MOUNTED.equals(
                Environment.getExternalStorageState())) {
            return false;
        }
        //storage/emulated/0/Android/data/app_package_name/files
        File fileDir = App.instance().getExternalFilesDir("");
        if (fileDir == null) {
            fileDir = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/" + App.instance().getPackageName() + "/files");
        }
        return FileKit.delete(fileDir);
    }
}
