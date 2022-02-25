package com.example.wangzheng.binder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Create by wangzheng on 2018/5/30
 */
public class RemoteService extends Service {
    //MemoryFile
    AidlInterface.Stub binder = new AidlInterface.Stub<Integer,String>() {
        public String method(Integer arg) {
            return "service pid : " + android.os.Process.myPid()
                    + "\nbinder arg : " + arg;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                NativeLib.startDaemonProcess("0");
//            }
//        }).start();
    }
}
