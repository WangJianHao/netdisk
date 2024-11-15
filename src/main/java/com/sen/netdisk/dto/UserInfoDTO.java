package com.sen.netdisk.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/10 0:53
 */
@Data
public class UserInfoDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 总空间，单位byte
     */
    private Long totalSpace;


    /**
     * 密码，md5加密
     */
    private String password;

    /**
     * 注册时间
     */
    private Timestamp createTime;

    /**
     * 最后登录时间
     */
    private Timestamp lastLoginTime;


    /**
     * 是否为管理员
     */
    private Boolean isAdmin;

}
