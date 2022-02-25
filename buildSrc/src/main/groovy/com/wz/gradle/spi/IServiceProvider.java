package com.wz.gradle.spi;

/**
 * Create by wangzheng on 2022/1/7
 */
public interface IServiceProvider {
    void invoke(Object ...args);
}
