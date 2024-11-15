package com.sen.netdisk.common.constant;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:18
 */
public enum FileCategoryEnum implements IEnum<Integer> {
    VIDEO(1, "视频"),
    AUDIO(2, "音频"),
    PICTURE(3, "图片"),
    DOCUMENT(4, "文档"),
    OTHER(5, "其他");

    private final Integer code;
    private final String description;

    FileCategoryEnum(Integer code, String description) {
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

    public static FileCategoryEnum getByCode(Integer code) {
        Optional<FileCategoryEnum> first = Stream.of(FileCategoryEnum.values()).filter(fileCategoryEnum -> fileCategoryEnum.getCode().equals(code)).findFirst();
        return first.orElse(OTHER);
    }
}
