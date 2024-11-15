package com.sen.netdisk.common.exception;

import com.sen.netdisk.common.constant.ResultCodeEnum;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:22
 */
public class BusinessException extends RuntimeException {

    private Integer code;
    private Object[] args;

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause, Integer code, String message, Object... args) {
        super(message, cause);
        this.code = code;
        this.args = args;
    }

    public BusinessException(Integer code, String message, Object... args) {
        super(message);
        this.code = code;
        this.args = args;
    }

    public BusinessException(ResultCodeEnum exceptionEnum) {
        this(exceptionEnum.getCode(), exceptionEnum.getMessage());
    }

    public BusinessException(ResultCodeEnum exceptionEnum, Object... args) {
        this(exceptionEnum.getCode(), exceptionEnum.getMessage(), args);
    }

    public BusinessException(Throwable cause, ResultCodeEnum exceptionEnum, Object... args) {
        this(cause, exceptionEnum.getCode(), exceptionEnum.getMessage(), args);
    }

    @Override
    public String getMessage() {
        if (code != null) {
            if (args != null && args.length > 0) {
                return String.format(super.getMessage(), args);
            }
        }
        return super.getMessage();
    }
}
