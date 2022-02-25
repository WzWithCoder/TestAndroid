package com.example.wangzheng.http.callback;


import com.example.wangzheng.http.common.BaseBean;
import com.example.wangzheng.http.common.GsonUtil;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/7.
 */
public abstract class CompatCallback<T> extends AbsCallBack<T> {
    public CompatCallback(Class clazz) {
        mGenericType = clazz;
    }

    public void onResponse(Response response) throws Exception {
        String json = response.body().string();
        BaseBean result = GsonUtil.fromJson(json, BaseBean.class);
        if (result.Status == SUCCESS_CODE) {
            T data = GsonUtil.fromJson(json, mGenericType);
            sendSuccessMessage(data);
        } else {
            sendErrorMessage(result.Status, result.Msg);
        }
    }
}
