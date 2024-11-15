package com.sen.netdisk.dto.query;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 18:18
 */
@Data
public class ShareQuery {
    /**
     * 分享ID
     */
    private String shareId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 有效期类型：0-1天，1-7天，2-30天，3-永久有效
     */
    private Integer validType;

    /**
     * 失效时间
     */
    private Timestamp expireTime;

    /**
     * 分享时间
     */
    private Timestamp shareTime;

    /**
     * 分享码
     */
    private String code;

    /**
     * 浏览次数
     */
    private Long showCount;
}
