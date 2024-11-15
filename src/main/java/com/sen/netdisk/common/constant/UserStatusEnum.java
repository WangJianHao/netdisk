package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:32
 */
public enum UserStatusEnum implements IEnum {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer code;

    private final String description;

    UserStatusEnum(Integer code, String description) {
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
