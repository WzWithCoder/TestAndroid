package com.example.wangzheng.common;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Create by wangzheng on 2019/1/16
 */
public class SoftInputKit {

    public static void showInputMethod(Context context, View viewToken) {
        showInputMethod(context, viewToken, null);
    }

    public static void showInputMethod(Context context, View viewToken,
                                       final CallHandler<Integer> callHandler) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.showSoftInput(viewToken, InputMethodManager.RESULT_UNCHANGED_SHOWN,
                new ResultReceiver(viewToken.getHandler()) {
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        invokeCallHandler(callHandler, resultCode);
                    }
                })) {
            invokeCallHandler(callHandler, 0);
        }
    }

    public static void hideInputMethod(Context context, View viewToken) {
        hideInputMethod(context, viewToken, null);
    }

    public static void hideInputMethod(Context context, View viewToken,
                                       final CallHandler<Integer> callHandler) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.hideSoftInputFromWindow(viewToken.getWindowToken(), 0,
                new ResultReceiver(viewToken.getHandler()) {
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        invokeCallHandler(callHandler, resultCode);
                    }
                })) {
            invokeCallHandler(callHandler, 0);
        }
    }

    private static void invokeCallHandler(CallHandler<Integer> callHandler,
                                          final int resultCode) {
        if (callHandler != null) {
            callHandler.handle(
                    resultCode
            );
        }
    }
}
