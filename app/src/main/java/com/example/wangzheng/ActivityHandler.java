package com.example.wangzheng;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.wangzheng.ui.TestActivity;
import com.ymt.server.IRequestHandler;
import com.ymt.server.RequestContext;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by wangzheng on 2016/9/5.
 */

public class ActivityHandler implements IRequestHandler {
    private String uriSuffix = "Activity";
    private Context context = null;

    public ActivityHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean accept(String uri) {
        return uri.contains(uriSuffix);
    }

    @Override
    public void handle(RequestContext context) {
        try {
            openActivity();

            OutputStream os = context.getSocket().getOutputStream();
            PrintWriter writer = new PrintWriter(os);
            writer.println(context.getProtocol() + " 200 OK");
            String response = "App打开成功";
            writer.println("Content-Length:" + response.getBytes().length);
            writer.println("Content-Type:application/json;charset=utf-8");
            writer.println();
            writer.println(response);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openActivity() {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
