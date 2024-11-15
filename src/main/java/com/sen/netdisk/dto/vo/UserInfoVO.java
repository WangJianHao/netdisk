package com.sen.netdisk.dto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:30
 */
@Data
@ApiModel
public class UserInfoVO {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 管理员
     */
    @ApiModelProperty(value = "是否为管理员")
    private Boolean isAdmin;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 0-禁用，1-启用
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * 使用空间，单位byte
     */
    @ApiModelProperty(value = "使用空间")
    private Long useSpace;

    /**
     * 总空间，单位byte
     */
    @ApiModelProperty(value = "总空间")
    private Long totalSpace;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String headIcon;

    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间")
    private String createTime;

    /**
     * 上次登录时间
     */
    @ApiModelProperty(value = "上次登录时间")
    private String lastLoginTime;
}
