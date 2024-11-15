package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:21
 */
public enum FileStatusEnum implements IEnum {
    CODING(0, "转码中"),
    CODE_FAIL(1, "转码失败"),
    CODE_SUCCESS(2, "转码成功");

    private final Integer code;
    private final String description;

    FileStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getCodeString() {
        return code.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
