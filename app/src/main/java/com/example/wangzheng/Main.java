package com.example.wangzheng;

import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Create by wangzheng on 2021/8/13
 */
public class Main {
    public static void main(String[]args) {
        //app_process -Djava.class.path=/data/app/moe.haruue.test-1/base.apk /system/bin moe.haruue.Test
        //app_process -Djava.class.path=Helloworld.dex  /data/local/tmp com.zl.movepkgdemo.Helloworld
        String imei = getIMEI();
        while (true) {
            System.out.println("===========:"+imei+"----------");
        }
    }

    public static String getIMEI() {
        String command = "adb shell service call iphonesubinfo 4";
        return execShell(command);
    }

    public static String appProcess() {
        App context = App.instance();
        String path = null;
        try {
            path = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), 0).publicSourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String command = "app_process -Djava.class.path="+path+" /system/bin com.example.wangzheng.Main";
        return Main.execShell(command);
    }

    public static String execShell(String command) {
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str += line;
            }
            reader.close();
            int exitCode = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str.trim();
    }
}
