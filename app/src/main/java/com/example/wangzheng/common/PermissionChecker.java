package com.example.wangzheng.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class PermissionChecker {

    private FragmentActivity mContext;
    private AlertDialog mAlertDialog;
    private RxPermissions mRxPermissions;

    private boolean isChecking = false;
    private CallHandler mCall;
    private String[] mPermissions;

    private String mAlertMsg = "当前应用缺少必要权限。\n请点击‘设置’-‘权限’-打开所需权限。";


    public PermissionChecker(FragmentActivity context) {
        this.mContext = context;
        AppLifecycleMonitor.instance().addActivityLifecycle(
                mContext, new ActivityLifecycle() {
                    public void onActivityResumed(Activity activity) {
                        checkPermission(mCall, mPermissions);
                    }
                });
    }

    public static PermissionChecker from(FragmentActivity context){
        return new PermissionChecker(context);
    }

    public void checkPermission(final CallHandler call, String... permissions) {
        mCall = call;
        mPermissions = permissions;

        if (permissions == null) {
            return;
        }
        if (mAlertDialog != null &&
                mAlertDialog.isShowing()) {
            return;
        }
        if (isChecking) {
            return;
        }
        isChecking = true;
        if (mRxPermissions == null) {
            mRxPermissions = new RxPermissions(mContext);
        }
        mRxPermissions.request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    public void accept(Boolean granted) {
                        isChecking = false;
                        if (granted) {
                            call.handle(granted);
                            AppLifecycleMonitor.instance()
                                    .removeActivityLifecycle(mContext);
                        } else {
                            showAlertDialog();
                        }
                    }
                });
    }

    private AlertDialog showAlertDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.show();
            return mAlertDialog;
        }
        return mAlertDialog = new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle("帮助")
                .setMessage(mAlertMsg)
                .setPositiveButton("设置", (dialog, which)-> {
                        jumpAppSetting(mContext);
                })
                .setNegativeButton("返回", (dialog, which)-> {
                        mContext.finish();
                }).show();
    }

    public static void jumpAppSetting(Activity context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
