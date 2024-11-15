package com.sen.netdisk.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 23:33
 */
@Data
public class FileShareDTO {

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

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件封面
     */
    private String fileCover;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 0-文件，1-目录
     */
    private Integer folderType;

    /**
     * 文件分类：1-视频，2-音频，3-图片，4-文档，5-其他
     */
    private Integer fileCategory;

    /**
     * 文件类型：1-视频，2-音频，3-图片，4-pdf，5-doc，6-excel，7-txt，8-code，9-zip，10-其他
     */
    private Integer fileType;

}
