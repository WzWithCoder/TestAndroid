package com.example.wangzheng.common;

import com.example.wangzheng.http.common.BaseResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Create by wangzheng on 2018/11/1
 */
public abstract class ResultCallback<T>
        implements Callback<BaseResult<T>> {
    @Override
    public void onResponse(Call<BaseResult<T>> call,
                           Response<BaseResult<T>> response) {
        BaseResult<T> data = response.body();
        if (data == null) {
            onError("数据异常");
        } else if (data.Status == 200) {
            onSuccess(data.Result);
        } else {
            onError(data.Msg);
        }
    }

    @Override
    public void onFailure(Call<BaseResult<T>> call, Throwable throwable) {
        onError(throwable.getMessage());
    }

    public abstract void onSuccess(T t);

    public abstract void onError(String msg);
}
