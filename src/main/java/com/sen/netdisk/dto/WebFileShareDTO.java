package com.sen.netdisk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 17:01
 */
@Data
public class WebFileShareDTO {

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "用户分享时间")
    private String shareTime;

    @ApiModelProperty(value = "分享失效时间")
    private String expireTime;

    @ApiModelProperty(value = "分享文件ID")
    private String fileId;

    @ApiModelProperty(value = "分享文件名称")
    private String fileName;

    @ApiModelProperty(value = "头像")
    private String headIcon;

    private Boolean isCurrentUser;
}
