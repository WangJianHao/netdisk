package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:25
 */
public enum DelFlagEnum implements IEnum<Integer> {
    NORMAL(0, "正常"),
    RECOVERY(1, "回收站"),
    DELETE(2, "删除");

    private final Integer code;
    private final String description;

    DelFlagEnum(Integer code, String description) {
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
