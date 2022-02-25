package com.example.wangzheng.common;

/**
 * Created by wangzheng on 2016/12/5.
 */

public interface Callable<K, V> {
    V call(K k);
}
