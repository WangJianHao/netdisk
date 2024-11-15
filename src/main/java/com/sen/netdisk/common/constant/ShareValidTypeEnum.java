package com.sen.netdisk.common.constant;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 18:26
 */
public enum ShareValidTypeEnum implements IEnum<Integer> {
    ONE_DAY(0, "1天有效", 1),
    SEVEN_DAY(1, "7天有效", 7),
    THIRTY_DAY(2, "30天有效", 30),
    FOREVER(3, "永久有效", null);

    private final Integer code;
    private final String description;

    private final Integer day;

    ShareValidTypeEnum(Integer code, String description, Integer day) {
        this.code = code;
        this.description = description;
        this.day = day;
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

    public Integer getDay() {
        return day;
    }

    public static ShareValidTypeEnum getEnumByCode(Integer code) {
        Optional<ShareValidTypeEnum> shareValidTypeEnumOptional = Stream.of(ShareValidTypeEnum.values()).filter(shareValidTypeEnum -> shareValidTypeEnum.getCode().equals(code)).findFirst();
        return shareValidTypeEnumOptional.orElse(null);
    }
}
