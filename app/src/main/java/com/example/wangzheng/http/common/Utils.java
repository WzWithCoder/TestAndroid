package com.example.wangzheng.http.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by wangzheng on 2016/7/29.
 */
public class Utils {
    public static byte[] toBytes(InputStream is) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = 0;
        try {
            while (-1 != (len = is.read(buffer))) {
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output.toByteArray();
    }


    public static OkHttpClient addResponseProgressListener(OkHttpClient client, final ProgressListener progressListener) {
        OkHttpClient cloneClient = client.newBuilder().build();//克隆
        cloneClient.networkInterceptors().add(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return cloneClient;
    }

}
