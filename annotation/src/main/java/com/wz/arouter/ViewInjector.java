package com.wz.arouter;

/**
 * author: wangzheng
 * create on: 2018/1/10
 * description: inject code
 */
public interface ViewInjector<T> {
    void inject(Object source, T target);
}
