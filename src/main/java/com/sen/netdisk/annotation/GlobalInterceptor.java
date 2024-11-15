package com.sen.netdisk.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 15:02
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GlobalInterceptor {

    boolean validParam() default false;
}
