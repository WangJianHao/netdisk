package com.sen.netdisk.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author sensen
 * @Description
 * @date 2024-08-11
 */

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
@TableName(value = "tbas_fileinfo")
public class FileInfoDO implements Serializable {
    private static final long serialVersionUID = 1228722701390654542L;

    /**
     * 文件ID
     */
    @TableId(value = "file_id")
    private String fileId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 文件MD5值
     */
    @TableField(value = "md5")
    private String md5;

    /**
     * 文件所在目录ID
     */
    @TableField(value = "parent_id")
    private String parentId;

    /**
     * 文件大小
     */
    @TableField(value = "file_size")
    private Long fileSize;

    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 文件封面
     */
    @TableField(value = "file_cover")
    private String fileCover;

    /**
     * 文件路径
     */
    @TableField(value = "file_path")
    private String filePath;

    /**
     * 0-文件，1-目录
     */
    @TableField(value = "folder_type")
    private Integer folderType;

    /**
     * 文件分类：1-视频，2-音频，3-图片，4-文档，5-其他
     */
    @TableField(value = "file_category")
    private Integer fileCategory;

    /**
     * 文件类型：1-视频，2-音频，3-图片，4-pdf，5-doc，6-excel，7-txt，8-code，9-zip，10-其他
     */
    @TableField(value = "file_type")
    private Integer fileType;

    /**
     * 0-转码中，1-转码失败，2-转码成功
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 0-正常，1-回收站，2-删除
     */
    @TableField(value = "del_flag")
    private Integer delFlag;

    /**
     * 进入回收站时间
     */
    @TableField(value = "recovery_time")
    private Timestamp recoveryTime;

    /**
     * 删除时间
     */
    @TableField(value = "del_time")
    private Timestamp delTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;

}
