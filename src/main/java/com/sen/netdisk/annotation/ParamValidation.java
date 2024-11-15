package com.sen.netdisk.annotation;

import com.sen.netdisk.common.constant.ParamValidationEnum;

import java.lang.annotation.*;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 13:22
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ParamValidation {
    /**
     * 匹配正则
     *
     * @return 正则表达式
     */
    ParamValidationEnum regx() default ParamValidationEnum.NO;

    /**
     * 错误信息
     *
     * @return 错误信息
     */
    String message() default "";


}
