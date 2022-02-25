package com.example.wangzheng.common;

import android.util.Log;

import com.example.wangzheng.http.HttpProxy;
import com.example.wangzheng.http.common.BaseResult;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Create by wangzheng on 2018/10/31
 */
public interface Api {
    Retrofit retrofit = new Retrofit.Builder()
            .client(HttpProxy.getOkHttpClient())
            .baseUrl("https://seller.ymatou.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    Api      api      = retrofit.create(Api.class);

    @GET("/api/system/ListEnvironmentConstant")
    Call<BaseResult<Enviroment>> getEnviroment();

    class Test {
        public static void test() {
            api.getEnviroment().enqueue(
                    new ResultCallback<Enviroment>() {
                        public void onSuccess(Enviroment env) {
                            CommonKit.toast(env.ProductPage);
                        }
                        public void onError(String msg) {
                            Log.e("xxxxx",msg);
                            CommonKit.toast(msg);
                        }
                    });
        }
    }

    class Enviroment {
        public String ProductPage;
    }
}
