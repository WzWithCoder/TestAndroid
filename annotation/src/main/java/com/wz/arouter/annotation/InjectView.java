package com.wz.arouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wangzheng on 2017/3/7.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface InjectView {
    int value() default -1;
}
