package com.sen.netdisk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 23:59
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingDTO implements Serializable {

    private String registerEmailTitle = "邮箱验证码";

    private String registerEmailContent = "您好，您的邮箱验证码是：%s，10分钟有效";

    //单位MB
    private Integer userInitTotalSpace = 50;

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public void setRegisterEmailTitle(String registerEmailTitle) {
        this.registerEmailTitle = registerEmailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public Integer getUserInitTotalSpace() {
        return userInitTotalSpace;
    }

    public void setUserInitTotalSpace(Integer userInitTotalSpace) {
        this.userInitTotalSpace = userInitTotalSpace;
    }
}
