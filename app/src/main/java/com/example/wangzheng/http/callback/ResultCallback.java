package com.example.wangzheng.http.callback;

import com.example.wangzheng.http.common.BaseBean;
import com.example.wangzheng.http.common.BaseResult;
import com.example.wangzheng.http.common.GsonUtil;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/7.
 */
public abstract class ResultCallback<T> extends AbsCallBack<T> {
    protected BaseBean result = null;

    public void onResponse(Response response) throws Exception {
        String json = response.body().string();
        result = GsonUtil.fromJson(json, BaseBean.class);
        if (result.Status == SUCCESS_CODE) {
            BaseResult<T> data = GsonUtil.fromJson(json,
                    GsonUtil.comboType(mGenericType,BaseBean.class));
            sendSuccessMessage(data.Result);
        } else {
            sendErrorMessage(result.Status, result.Msg);
        }
    }
}
