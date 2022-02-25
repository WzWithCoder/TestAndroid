package com.example.wangzheng.common;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.wangzheng.App;

/**
 * Create by wangzheng on 2018/7/26
 */
public class BroadcastKit {
    public static void registerReceiver(BroadcastReceiver receiver, String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        LocalBroadcastManager.getInstance(App.instance())
                .registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(App.instance())
                .unregisterReceiver(receiver);
    }

    public static void sendBroadcast(String action) {
        sendBroadcast(new Intent(action));
    }

    public static void sendBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(App.instance())
                .sendBroadcast(intent);
    }

    public static void sendBroadcastSync(Intent intent) {
        LocalBroadcastManager.getInstance(App.instance())
                .sendBroadcastSync(intent);
    }
}
