package com.sen.netdisk.entity;

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

/**
 * @author sensen
 * @Description
 * @date 2024-08-14
 */

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
@TableName(value = "tbas_share")
public class ShareInfoDO implements Serializable {
    private static final long serialVersionUID = 2052834730234768511L;

    /**
     * 分享ID
     */
    @TableId(value = "share_id")
    private String shareId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 文件ID
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 有效期类型：0-1天，1-7天，2-30天，3-永久有效
     */
    @TableField(value = "valid_type")
    private Integer validType;

    /**
     * 失效时间
     */
    @TableField(value = "expire_time")
    private Timestamp expireTime;

    /**
     * 分享时间
     */
    @TableField(value = "share_time")
    private Timestamp shareTime;

    /**
     * 分享码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 浏览次数
     */
    @TableField(value = "show_count")
    private Integer showCount;

}
