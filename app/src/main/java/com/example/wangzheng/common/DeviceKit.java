package com.example.wangzheng.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.UUID;

/**
 * https://developer.android.google.cn/training/articles/user-data-ids
 * 为避免app卸载重新安装，生成新的设备标识，AndroidQ请关闭沙箱
 * Create by wangzheng on 2019/8/21
 */
public class DeviceKit {
    public static final String TAG = DeviceKit.class.getSimpleName();
    private static final String DEVICE_ID = "DEVICE_ID";

    private static String mDeviceId = null;
    private static String mTempId = null;

    public static String getDeviceId(Context context) {
        //内存中的id不为空，直接返回，不再做查找
        if (!TextUtils.isEmpty(mDeviceId)) {
            return mDeviceId;
        }
        //应用管理中，可能会被用户手动清理
        mDeviceId = SpKit.read(DEVICE_ID, "");
        if (!TextUtils.isEmpty(mDeviceId)) {
            writeIdToSdcard(context, mDeviceId);
            return mDeviceId;
        }
        //SD卡中，可能会被用户手动删除
        mDeviceId = findIdBySdcard(context);
        if (!TextUtils.isEmpty(mDeviceId)) {
            SpKit.write(DEVICE_ID, mDeviceId);
            return mDeviceId;
        }
        //生成设备唯一标识
        mDeviceId = generateId(context);
        //系统版本小于AndroidQ，没有权限时返回为空
        if (!TextUtils.isEmpty(mDeviceId)) {
            //多处备份，防止丢失
            SpKit.write(DEVICE_ID, mDeviceId);
            writeIdToSdcard(context, mDeviceId);
            return mDeviceId;
        } else if (TextUtils.isEmpty(mTempId)) {
            //生成临时id，不存储
            mTempId = UUID.randomUUID().toString();
        }
        return mTempId;
    }

    private static String findIdBySdcard(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "not have READ_EXTERNAL_STORAGE");
            return "";
        }
        final File file = getDeviceIdFile();
        String deviceId = PropertieKit.of(file).read(DEVICE_ID);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = PropertieKit.getDefault().read(DEVICE_ID);
        }
        return deviceId;
    }

    private static void writeIdToSdcard(Context context, String deviceId) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "not have WRITE_EXTERNAL_STORAGE");
            return;
        }
        final String comments = "System file, do not modify!!!";
        final File file = getDeviceIdFile();
        //根目录设备文件，多应用共享
        PropertieKit.of(file).write(DEVICE_ID, deviceId, comments);
        //多处备份，防止丢失
        PropertieKit.getDefault().write(DEVICE_ID, deviceId, comments);
    }

    private static String generateId(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            try {//无权限时为空
                return generateDeviceId(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//Q以后的版本
        return UUID.randomUUID().toString();
    }

    private static String generateDeviceId(Context context) throws Exception {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                ) + tm.getSimSerialNumber();
            }
            String uuid = UUID.nameUUIDFromBytes(
                    deviceId.getBytes()).toString();
            return uuid;
        } else {
            Log.e(TAG, "not have READ_PHONE_STATE");
            return null;
        }
    }

    private static File getDeviceIdFile() {
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir, ".system_id");
        return file;
    }
}
