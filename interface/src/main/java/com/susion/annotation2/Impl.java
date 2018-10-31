package com.susion.annotation2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by susion on 2018/10/26.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Impl {

    /**
     * 唯一名字
     */
    String name() default "";
}
