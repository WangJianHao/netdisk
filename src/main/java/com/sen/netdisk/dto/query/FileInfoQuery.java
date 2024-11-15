package com.sen.netdisk.dto.query;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:34
 */
@Data
public class FileInfoQuery {

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private String userId;

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

    /**
     * 0-转码中，1-转码失败，2-转码成功
     */
    private Integer status;

    /**
     * 0-正常，1-回收站，2-删除
     */
    private Integer delFlag;

    /**
     * 文件MD5值
     */
    private String md5;

    /**
     * 父目录ID
     */
    private String parentId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件ID集合
     */
    private List<String> fileIdList;

    private List<String> excludeFileIdList;


    /**
     * 文件名称模糊搜索
     */
    private String fileNameFuzzy;
}
