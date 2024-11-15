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

/**
 * @author sensen
 * @Description
 * @date 2024-08-07
 */

@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"}, ignoreUnknown = true)
@TableName(value = "tusr_userinfo")
public class UserInfoDO implements Serializable {
    private static final long serialVersionUID = 6364601489887971188L;

    /**
     * 用户ID
     */
    @TableId("user_id")
    private String userId;

    /**
     * 昵称
     */
    @TableField(value = "nick_name")
    private String nickName;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 0-禁用，1-启用
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 使用空间，单位byte
     */
    @TableField(value = "use_space")
    private Long useSpace;

    /**
     * 总空间，单位byte
     */
    @TableField(value = "total_space")
    private Long totalSpace;

    /**
     * 用户头像
     */
    @TableField(value = "head_icon")
    private String headIcon;

    /**
     * 密码，md5加密
     */
    @TableField(value = "password")
    private String password;

    /**
     * 注册时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Timestamp createTime;

    /**
     * 上次登录时间
     */
    @TableField(value = "last_login_time")
    private Timestamp lastLoginTime;

}
