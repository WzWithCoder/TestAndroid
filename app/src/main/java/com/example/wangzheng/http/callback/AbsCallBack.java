package com.example.wangzheng.http.callback;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.example.wangzheng.http.common.GsonUtil;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/29.
 */
public abstract class AbsCallBack<T> {
    public final static int ERROR_CODE       = -1;
    public final static int SUCCESS_CODE     = 200;
    public final static int IN_PROGRESS_CODE = -2;

    public Type mGenericType = null;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            try {
                handleDispatcher(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void handleDispatcher(Message msg) throws Exception {
        if (msg.what == SUCCESS_CODE) {
            onSuccess((T) msg.obj);
        } else if (msg.what == IN_PROGRESS_CODE) {
            onProgress((int) msg.obj);
        } else {
            String errorMsg = (String) msg.obj;
            if (TextUtils.isEmpty(errorMsg)) {
                errorMsg = "操作异常";
            }
            onError(errorMsg);
        }
    }

    private Message obtainMessage(int what, Object data) {
        return mHandler.obtainMessage(what, data);
    }

    public void sendErrorMessage(String msg) {
        sendErrorMessage(ERROR_CODE, msg);
    }

    public void sendErrorMessage(int code, String msg) {
        mHandler.sendMessage(obtainMessage(code, msg));
    }

    public void sendSuccessMessage(T data) {
        mHandler.sendMessage(obtainMessage(SUCCESS_CODE, data));
    }

    public void sendProgressMessage(int progress) {
        mHandler.sendMessage(obtainMessage(IN_PROGRESS_CODE, progress));
    }

    public AbsCallBack() {
        mGenericType = GsonUtil.getSuperclassType(getClass());
    }

    public abstract void onResponse(Response response) throws Exception;

    public abstract void onSuccess(T data);

    public abstract void onError(String msg);

    public void onProgress(int progress) {

    }
}
