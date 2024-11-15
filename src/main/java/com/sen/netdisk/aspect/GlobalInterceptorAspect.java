package com.sen.netdisk.aspect;

import com.sen.netdisk.annotation.GlobalInterceptor;
import com.sen.netdisk.annotation.ParamValidation;
import com.sen.netdisk.common.constant.ParamValidationEnum;
import com.sen.netdisk.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.springframework.cglib.core.Constants.*;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 13:37
 */
@Aspect
@Component
public class GlobalInterceptorAspect {

    private static final Logger log = LoggerFactory.getLogger(GlobalInterceptorAspect.class);

    @Pointcut("@annotation(com.sen.netdisk.annotation.GlobalInterceptor)")
    public void interceptor() {

    }

    @Before("interceptor()")
    public void doBefore(JoinPoint point) throws Exception {
        try {
            Object target = point.getTarget();
            Object[] args = point.getArgs();
            String methodName = point.getSignature().getName();
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            GlobalInterceptor globalInterceptor = method.getAnnotation(GlobalInterceptor.class);
            if (Objects.isNull(globalInterceptor)) {
                return;
            }
            if (globalInterceptor.validParam()) {
                validateParam(method, args);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private void validateParam(Method method, Object[] args) throws BusinessException {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            ParamValidation paramValidation = parameter.getAnnotation(ParamValidation.class);
            if (Objects.isNull(paramValidation)) {
                continue;
            }
            String type = parameter.getParameterizedType().getTypeName();
            if (StringUtils.equals(TYPE_STRING.toString(), type)) {
                checkValue(arg, paramValidation);
            } else if (StringUtils.equals(TYPE_LONG.toString(), type) || StringUtils.equals(TYPE_INTEGER.toString(), type)) {
                checkValue(arg, paramValidation);
            } else {
                checkObjValue(parameter, arg);
            }
        }
    }

    private void checkObjValue(Parameter parameter, Object arg) throws BusinessException {
        try {
            String typeName = parameter.getParameterizedType().getTypeName();
            Class<?> clazz = Class.forName(typeName);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                ParamValidation fieldParamValidation = field.getAnnotation(ParamValidation.class);
                if (Objects.isNull(fieldParamValidation)) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(arg);//arg是实例对象，即field所在的obj，resultValue为该field对应的值
                checkValue(resultValue, fieldParamValidation);
            }
        } catch (Exception e) {
            log.error("校验参数失败", e);
            throw new BusinessException("校验不通过");
        }
    }

    private void checkValue(Object value, ParamValidation fieldParamValidation) throws BusinessException {
        boolean isEmpty = value == null || StringUtils.isEmpty(value.toString());

        ParamValidationEnum regx = fieldParamValidation.regx();
        String message = fieldParamValidation.message();
        if (!isEmpty && !Pattern.matches(regx.getRegx(), String.valueOf(value))) {
            throw new BusinessException(message);
        }
    }

}
