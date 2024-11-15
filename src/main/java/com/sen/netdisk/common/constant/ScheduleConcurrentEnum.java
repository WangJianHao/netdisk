package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/12 0:23
 */
public enum ScheduleConcurrentEnum implements IEnum<String> {
    ENABLE("0", "允许并发执行"),
    DISABLE("1", "不允许并发执行");

    private final String code;

    private final String description;

    ScheduleConcurrentEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCodeString() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
