package com.example.wangzheng.http.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/7.
 */
public abstract class BitmapCallback extends AbsCallBack<Bitmap> {
    @Override
    public void onResponse(Response response) throws Exception {
        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
        sendSuccessMessage(bitmap);
    }
}
