package com.sen.netdisk.component;

import com.sen.netdisk.annotation.ParamValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 14:39
 */
public class ParamValidator implements ConstraintValidator<ParamValidation, String> {

    @Override
    public void initialize(ParamValidation constraintAnnotation) {
        // 初始化逻辑，例如读取配置信息等
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return false;
    }
}
