package com.sen.netdisk.common;


import com.sen.netdisk.common.constant.ResultCodeEnum;
import com.sen.netdisk.common.utils.DateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

/**
 * 通用返回对象
 *
 * @author sensen
 * @date 2021-01-01
 */
@NoArgsConstructor
public class SenCommonResponse<T> {

    @ApiModelProperty(value = "返回的code")
    private long code;

    @ApiModelProperty(value = "错误信息")
    private String message;

    @ApiModelProperty(value = "数据")
    private T data;

    @ApiModelProperty(value = "时间戳")
    private long timestamp;

    protected SenCommonResponse(long code, String message, T data) {
        this(code, message, data, DateUtil.getCurrentTimestamp());
    }

    protected SenCommonResponse(long code, String message, T data, long timestamp) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    /**
     * 成功返回结果
     */
    public static <T> SenCommonResponse<T> success() {
        return new SenCommonResponse<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), null);
    }


    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> SenCommonResponse<T> success(T data) {
        return new SenCommonResponse<T>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 成功返回结果
     *
     * @param data    获取的数据
     * @param message 提示信息
     */
    public static <T> SenCommonResponse<T> success(T data, String message) {
        return new SenCommonResponse<T>(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     */
    public static <T> SenCommonResponse<T> failed(ResultCodeEnum errorCode) {
        return new SenCommonResponse<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> SenCommonResponse<T> failed(String message) {
        return new SenCommonResponse<T>(ResultCodeEnum.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     *
     * @param errorCode 错误码
     * @param message   提示信息
     */
    public static <T> SenCommonResponse<T> failed(ResultCodeEnum errorCode, String message) {
        return new SenCommonResponse<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> SenCommonResponse<T> failed() {
        return failed(ResultCodeEnum.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> SenCommonResponse<T> validateFailed() {
        return failed(ResultCodeEnum.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> SenCommonResponse<T> validateFailed(String message) {
        return new SenCommonResponse<T>(ResultCodeEnum.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> SenCommonResponse<T> unauthorized(T data) {
        return new SenCommonResponse<T>(ResultCodeEnum.UNAUTHORIZED.getCode(), ResultCodeEnum.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> SenCommonResponse<T> forbidden(T data) {
        return new SenCommonResponse<T>(ResultCodeEnum.FORBIDDEN.getCode(), ResultCodeEnum.FORBIDDEN.getMessage(), data);
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
