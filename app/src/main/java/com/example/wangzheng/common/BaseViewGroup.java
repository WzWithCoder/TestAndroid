package com.example.wangzheng.common;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

/**
 * Create by wangzheng on 1/28/21
 */
public class BaseViewGroup extends ViewGroup {
    public BaseViewGroup(Context context) {
        super(context);
        Log.e("BaseViewGroup","BaseViewGroup");
        CommonKit.toast("BaseViewGroup1");
    }

    public BaseViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        CommonKit.toast("BaseViewGroup");
        Log.e("BaseViewGroup","BaseViewGroup2");
    }

    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CommonKit.toast("BaseViewGroup");
        Log.e("BaseViewGroup","BaseViewGroup3");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        CommonKit.toast("BaseViewGroup");
        Log.e("BaseViewGroup","BaseViewGroup4");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
