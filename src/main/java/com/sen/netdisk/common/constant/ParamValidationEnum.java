package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 13:38
 */
public enum ParamValidationEnum {
    NO("", "不校验"),
    IP("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b", "IP地址"),
    EMAIL("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?", "邮件地址"),
    PASSWORD("^(?=.\\d)(?=.[a-z])(?=.*[A-Z])[\\da-zA-Z~!@#$%^&*_].{8,15}$", "密码校验，"),
    ACCOUNT("^[0-9a-zA-Z_](1,)$", "字母开头，由数字，英文字母或者下划线组成"),
    CHINESE("^[\\u4e00-\\u9fa5]{0,}$", "仅中文"),
    ID_CARD("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$", "身份证号码校验"),
    DATE("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$", "yyyy-mm-dd 日期校验"),
    MONEY("^[0-9]+(.[0-9]{2})?$", "金额"),
    TELEPHONE("^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$", "手机号码"),
    ;


    private final String regx;
    private final String description;


    ParamValidationEnum(String regx, String description) {
        this.regx = regx;
        this.description = description;
    }

    public String getRegx() {
        return regx;
    }

    public String getDescription() {
        return description;
    }
}
