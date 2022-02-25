package com.example.wangzheng.http.callback;

import android.text.TextUtils;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/7.
 */
public abstract class StringCallback extends AbsCallBack<String> {

    public void onResponse(Response response) throws Exception {
        String string = response.body().string();
        if (TextUtils.isEmpty(string)) {
            sendErrorMessage("");
        } else {
            sendSuccessMessage(string);
        }
    }
}
