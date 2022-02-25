package com.example.wangzheng;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.example.wangzheng.common.AppLifecycleMonitor;
import com.example.wangzheng.common.CommonKit;
import com.example.wangzheng.common.Reflector;

/**
 * Created by wangzheng on 2016/9/23.
 */

public class App extends MultiDexApplication {
    private static App instance = null;

    public static App instance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        String packageName = getPackageName();
        String processName = CommonKit.getProcessName(android.os.Process.myPid());
        if (TextUtils.equals(packageName, processName)) {
            //主进程
            UncaughtExceptionHandler.apply();
            AppLifecycleMonitor.apply(this);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            Log.d("xxx","ShutdownHook");
        }));
    }

//    @Override
//    public Resources getResources() {
//        Resources resources = DelegateResources
//                .with(super.getResources());
//        return resources;
//    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        try {
//            PluginLoader.test(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
