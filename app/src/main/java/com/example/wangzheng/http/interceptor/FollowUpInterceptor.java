package com.example.wangzheng.http.interceptor;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okhttp 307-POST重定向
 * Created by wangzheng on 2021/05/06.
 */
public final class FollowUpInterceptor implements Interceptor {
    private OkHttpClient client;

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        Response response = chain.proceed(request);
        if (canRedirect(request, response)) {
            response = followUpHandle(
                    request,
                    response);
        }
        return response;
    }

    boolean canRedirect(Request request, Response response) {
        if (client == null) return false;
        ///只处理307-POST重定向
        int code = response.code();
        String method = request.method();
        return "POST".equalsIgnoreCase(method)
                && code == 307;
    }

    private Response followUpHandle(Request request
            , Response response) throws IOException{
        ///获取重定向的地址
        Headers headers = response.headers();
        String location = headers.get("Location");
        ///重新构建请求
        Request newRequest = request
                .newBuilder()
                .url(location)
                .build();
        ///重定向请求 可跨协议
        ///newResponse = chain.proceed(request);
        Response newResponse = client
                .newCall(newRequest)
                .execute();
        return newResponse;
    }
}