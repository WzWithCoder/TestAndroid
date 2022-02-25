package com.example.wangzheng.http;

import android.text.TextUtils;
import android.util.Log;

import com.example.wangzheng.common.Md5Kit;
import com.example.wangzheng.http.callback.AbsCallBack;
import com.example.wangzheng.http.common.HttpsManager;
import com.example.wangzheng.http.common.ProgressListener;
import com.example.wangzheng.http.common.ProgressRequestBody;
import com.example.wangzheng.http.common.URLCoder;
import com.example.wangzheng.http.common.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by wangzheng on 2016/7/8.
 */
public abstract class HttpProxy {
    private volatile static OkHttpClient mOkHttpClient = null;

    private MediaType OCTET_TYPE = MediaType.parse("application/octet-stream");
    private MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private MediaType FORM_URLEN = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    public HttpProxy() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);
        /*final CacheControl.Builder cacheBuilder = new CacheControl.Builder();
        cacheBuilder.noCache();//不使用缓存，全部走网络
        cacheBuilder.noStore();//不使用缓存，也不存储缓存
        cacheBuilder.onlyIfCached();//只使用缓存
        cacheBuilder.noTransform();//禁止转码
        cacheBuilder.maxAge(10, TimeUnit.MILLISECONDS);//指示客户机可以接收生存期不大于指定时间的响应。
        cacheBuilder.maxStale(10, TimeUnit.SECONDS);//指示客户机可以接收超出超时期间的响应消息
        cacheBuilder.minFresh(10, TimeUnit.SECONDS);//指示客户机可以接收响应时间小于当前时间加上指定时间的响应。
        CacheControl cache = cacheBuilder.build();*/
        /*File cacheFile = Storage.getCacheDir("http_data");
        Cache cache = new Cache(cacheFile, 100 * 1024 * 1024);
        builder.cache(cache).addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (Toolkit.canNetwork()) {
                    int maxTime = 24 * 60 * 60;
                    return chain.proceed(request).newBuilder()
                            .header("Cache-Control", "max-age=" + maxTime)
                            .removeHeader("pragma")
                            .build();
                } else {
                    Request newRequest = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                    return chain.proceed(newRequest);
                }
            }
        });*/
        HttpsManager.SSLParams defaultSSL = HttpsManager.allowAllSSL();
        if (defaultSSL != null) {
            builder.hostnameVerifier(new HttpsManager.UnSafeHostnameVerifier())
                    .sslSocketFactory(defaultSSL.mSSLSocketFactory, defaultSSL.mTrustManager);
        }
        mOkHttpClient = builder.build();
    }

    public static void setHttpClient(OkHttpClient httpClient) {
        mOkHttpClient = httpClient;
    }

    public static OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            new HttpRequest();
        }
        return mOkHttpClient;
    }

    public void post(String url, JSONObject jsonParam, Map<String, String> params, Map<String, String> headers, AbsCallBack callback) {
        MediaType mediaType = jsonParam != null ? JSON_TYPE : FORM_URLEN;
        RequestBody body = jsonParam != null ?
                RequestBody.create(mediaType, jsonParam.toString()) :
                RequestBody.create(mediaType, URLCoder.format(params, URLCoder.UTF_8));
        Request.Builder builder = new Request.Builder().post(body).tag(url);

        if (jsonParam != null && params != null) {
            url = URLCoder.format(url, params);
        }
        request(url, headers, builder, callback);
    }

    public void get(String url, Map<String, String> params, Map<String, String> headers, AbsCallBack callback) {
        Request.Builder builder = new Request.Builder().get().tag(url);
        request(URLCoder.format(url, params), headers, builder, callback);
    }

    public void upload(String url, Map<String, String> headers, Map<String, Object> params, AbsCallBack callback) {
        MultipartBody.Builder bodyBuilder = getMultipartBody(params, callback);
        Request.Builder builder = new Request.Builder().post(bodyBuilder.build()).tag(url);
        request(url, headers, builder, callback);
    }

    public void uploadSync(String url, Map<String, String> headers, Map<String, Object> params, AbsCallBack callback) {
        MultipartBody.Builder bodyBuilder = getMultipartBody(params, callback);
        Request.Builder builder = new Request.Builder().post(bodyBuilder.build()).tag(url);
        requestSync(url, headers, builder, callback);
    }

    private MultipartBody.Builder getMultipartBody(Map<String, Object> params, AbsCallBack callback) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof String) {
                    bodyBuilder.addFormDataPart(entry.getKey(), (String) entry.getValue());
                } else if (entry.getValue() instanceof File
                        || entry.getValue() instanceof InputStream) {
                    buildMultipartBody(bodyBuilder, entry, callback);
                }
            }
        }
        return bodyBuilder;
    }

    private void buildMultipartBody(MultipartBody.Builder builder, Map.Entry<String, Object> entry, final AbsCallBack callback) {
        RequestBody fileBody = null;
        String fileName = null;
        if (entry.getValue() instanceof InputStream) {
            byte[] bytes = Utils.toBytes((InputStream) entry.getValue());
            fileBody = RequestBody.create(OCTET_TYPE, bytes);
            fileName = Md5Kit.md5(bytes);
        } else if (entry.getValue() instanceof File) {
            File file = (File) entry.getValue();
            fileBody = RequestBody.create(OCTET_TYPE, file);
            fileName = Md5Kit.md5(file);
        }
        fileName = "md5-" + (TextUtils.isEmpty(fileName) ? System.currentTimeMillis() : fileName) + ".jpg";
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(fileBody, new ProgressListener() {
            public void onProgress(final long bytesWritten, final long contentLength) {
                callback.sendProgressMessage((int) (bytesWritten * 100 / contentLength));
            }
        });
        builder.addFormDataPart(entry.getKey(), fileName, progressRequestBody);
    }

    public void download(String url, Map<String, String> headers, Map<String, String> params, AbsCallBack callback) {
        Request.Builder builder = new Request.Builder().get().tag(url);
        request(URLCoder.format(url, params), headers, builder, callback);
    }

    public void downloadSync(String url, Map<String, String> headers, Map<String, String> params, AbsCallBack callback) {
        Request.Builder builder = new Request.Builder().get().tag(url);
        requestSync(URLCoder.format(url, params), headers, builder, callback);
    }

    public void requestSync(String url, Map<String, String> headers, Request.Builder builder, final AbsCallBack callback) {
        try {
            if (headers == null) headers = new HashMap<>();
            headers.put(URLCoder.USER_AGENT, getUserAgent());
            builder.headers(Headers.of(headers));

            builder.url(complementUrl(url));
            Response response = mOkHttpClient.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                Response cacheResponse = response.cacheResponse();
                callback.onResponse(cacheResponse == null ? response.networkResponse() : cacheResponse);
            } else {
                errorDispatcher(callback, response.message());
            }
        } catch (Exception e) {
            errorDispatcher(callback, e.getMessage());
        }
    }

    public void request(String url, Map<String, String> headers, Request.Builder builder, final AbsCallBack callback) {
        if (headers == null) headers = new HashMap<>();
        headers.put(URLCoder.USER_AGENT, getUserAgent());
        builder.headers(Headers.of(headers));
        builder.url(complementUrl(url));
        mOkHttpClient.newCall(builder.build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                if (call != null && call.isCanceled()) return;
                errorDispatcher(callback, e.getMessage());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (call != null && call.isCanceled()) return;
                if (response.isSuccessful()) {
                    try {
//                        Response cacheResponse = response.cacheResponse();
                        callback.onResponse(response);
                    } catch (Exception e) {
                        errorDispatcher(callback, e.getMessage());
                    }
                } else {
                    errorDispatcher(callback, response.message());
                }
            }
        });
    }

    private void errorDispatcher(AbsCallBack handler, String failMsg) {
        if (!TextUtils.isEmpty(failMsg))
            Log.e("okhttp", failMsg);
        handler.sendErrorMessage(failMsg);
    }

    public void cancelTasker(String taskerId) {
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (isEqualTask(taskerId, call)) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (isEqualTask(taskerId, call)) {
                call.cancel();
            }
        }
    }

    private boolean isEqualTask(String taskerId, Call call) {
        return TextUtils.equals(taskerId, (String) call.request().tag());
    }

    private String complementUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (!url.startsWith("/")) url = "/" + url;
            url = getHostAddress() + url;
        }
        return URLCoder.format(url, getBaseParams());
    }

    public abstract Map<String, String> getBaseParams();

    public abstract String getUserAgent();

    public abstract String getHostAddress();
}
