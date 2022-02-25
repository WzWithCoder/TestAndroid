package com.example.wangzheng.plugin;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author wangzheng
 * @date 17/2/21
 */
public class HandlerCallback implements Handler.Callback {

    int ActivityThread_H_LAUNCH_ACTIVITY = 100;

    @Override
    public boolean handleMessage(Message msg) {
        Log.e("Main", "handleMessage what = " + msg.what);
        if (msg.what == ActivityThread_H_LAUNCH_ACTIVITY) {
            handleLaunchActivity(msg);
        }
        return false;
    }

    private void handleLaunchActivity(Message msg) {
        Log.e("Main", "handleLaunchActivity方法 拦截");
        try {
            Object obj = msg.obj;//ActivityClientRecord
            //把替身恢复成真身
            Intent intent = (Intent) ReflectKit.getFieldValue(obj, "intent");
            Intent target = intent.getParcelableExtra(PluginLoader.TARGET_INTENT);
            if(target != null){
                intent.setComponent(target.getComponent());
            }
            Log.e("Main", "target = " + target);
        } catch (Exception e) {
            throw new RuntimeException("hook launch activity failed", e);
        }
    }

}
