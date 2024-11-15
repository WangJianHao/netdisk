package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:16
 */
public enum FolderTypeEnum implements IEnum {
    FILE(0, "文件"),
    FOLDER(1, "目录");

    private final Integer code;
    private final String description;

    FolderTypeEnum(Integer code, String description) {
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
