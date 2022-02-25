package com.example.wangzheng.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class IActivityManagerHandler implements InvocationHandler {

    Context mContext;
    Object mActivityManagerNative;

    public IActivityManagerHandler(Context context,Object activityManagerNative) {
        mContext = context;
        mActivityManagerNative = activityManagerNative;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }

            Intent rawIntent = (Intent) args[index];
            //创建一个要被掉包的Intent
            Intent newIntent = new Intent();
            //替身Activity的包名, 也就是我们自己的"包名"
            String hostPackage = mContext.getPackageName();
            //Target Activity临时替换为 PluginHoldActivity
            ComponentName componentName = new ComponentName(
                    hostPackage, PluginHoldActivity.class.getName());
            newIntent.setComponent(componentName);
            //暂存Target Activity
            newIntent.putExtra(PluginLoader.TARGET_INTENT, rawIntent);
            //换掉Intent, 欺骗AMS校驗
            args[index] = newIntent;
            Log.e("Main","args[index] hook = " + args[index]);
        }
        return method.invoke(mActivityManagerNative, args);
    }
}
