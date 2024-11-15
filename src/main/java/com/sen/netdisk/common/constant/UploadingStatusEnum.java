package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 18:38
 */
public enum UploadingStatusEnum implements IEnum<String> {
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading", "上传中"),
    UPLOAD_FINISH("upload_finish", "上传完成");


    private final String code;

    private final String description;

    UploadingStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getCodeString() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.getCodeString();
    }
}
