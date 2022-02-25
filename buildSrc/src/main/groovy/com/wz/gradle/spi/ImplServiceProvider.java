package com.wz.gradle.spi;


/**
 * Create by wangzheng on 2022/1/7
 */
public class ImplServiceProvider implements IServiceProvider {
    @Override
    public void invoke(Object... args) {
        System.out.println(args[0].toString());
    }
}
