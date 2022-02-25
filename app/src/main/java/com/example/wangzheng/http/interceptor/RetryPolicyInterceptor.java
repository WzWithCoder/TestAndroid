package com.example.wangzheng.http.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * retry policy when request failed
 * Created by wangzheng on 2016/6/28.
 */
public class RetryPolicyInterceptor implements Interceptor {
    public int maxNum;

    public RetryPolicyInterceptor(int maxNum) {
        this.maxNum = maxNum;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);//原始请求
        int tryCount = 0;
        while (!response.isSuccessful() && tryCount < maxNum) {
            Log.d("RetryPolicyInterceptor",
                    "Request is not successful - " + tryCount);
            tryCount++;
            response = chain.proceed(request);
        }
        return response;
    }
}
