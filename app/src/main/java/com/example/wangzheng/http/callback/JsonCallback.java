package com.example.wangzheng.http.callback;


import com.example.wangzheng.http.common.GsonUtil;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/7.
 */
public abstract class JsonCallback<T> extends AbsCallBack<T> {

    public void onResponse(Response response) throws Exception {
        String json = response.body().string();
        T data = GsonUtil.fromJson(json, mGenericType);
        if (data != null) {
            sendSuccessMessage(data);
        } else {
            sendErrorMessage(null);
        }
    }
}
