package com.example.wangzheng.uiHandleBridger;

/**
 * Create by wangzheng on 2021/7/12
 */
public interface Call<K, V> {
    V invoke(K k);
}
