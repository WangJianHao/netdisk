package com.sen.netdisk.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 19:24
 */
@Data
public class MailDTO {

    /**
     * 发件人
     */
    private String from;

    /**
     * 收件人
     */
    private String to;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String text;

}
