package com.example.wangzheng.http;

import com.example.wangzheng.http.callback.AbsCallBack;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzheng on 2016/7/8.
 */
public class HttpRequest {
    private static HttpProxy mHttpProxy = new HttpProxy() {
        @Override
        public String getUserAgent() {
            return "wangzheng";
        }

        @Override
        public String getHostAddress() {
            return "";
        }

        @Override
        public Map<String, String> getBaseParams() {
            Map<String, String> params = new HashMap<>();
            return params;
        }
    };

    public static void post(String url, JSONObject jsonParam, Map<String, String> params, AbsCallBack callback) {
        mHttpProxy.post(url, jsonParam, params, null, callback);
    }

    public static void post(String url, JSONObject jsonParam, AbsCallBack callback) {
        post(url, jsonParam, null, callback);
    }

    public static void post(String url, Map<String, String> params, AbsCallBack callback) {
        post(url, null, params, callback);
    }

    public static void get(String url, Map<String, String> params, AbsCallBack callback) {
        mHttpProxy.get(url, params, null, callback);
    }

    public static void get(String url, AbsCallBack callback) {
        get(url, null, callback);
    }

    public static void upload(String url, Map<String, Object> params, AbsCallBack callback) {
        mHttpProxy.upload(url, null, params, callback);
    }

    public static void uploadSync(String url, Map<String, Object> params, AbsCallBack callback) {
        mHttpProxy.uploadSync(url, null, params, callback);
    }

    public static void download(String url, Map<String, String> params, AbsCallBack callback) {
        mHttpProxy.download(url, null, params, callback);
    }

    public static void download(String url, AbsCallBack callback) {
        download(url, null, callback);
    }

    public static void downloadSync(String url, Map<String, String> params, AbsCallBack callback) {
        mHttpProxy.downloadSync(url, null, params, callback);
    }

    public static void downloadSync(String url, AbsCallBack callback) {
        downloadSync(url, null, callback);
    }

    public static void cancelTasker(String taskerId) {
        mHttpProxy.cancelTasker(taskerId);
    }
}
