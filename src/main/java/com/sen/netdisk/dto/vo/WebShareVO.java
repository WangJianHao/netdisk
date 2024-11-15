package com.sen.netdisk.dto.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 16:35
 */
@Data
public class WebShareVO {

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

    @ApiModelProperty(value = "是否是当前用户")
    private Boolean isCurrentUser;

    @ApiModelProperty(value = "是否存在提取码")
    private Boolean existsCode = true;
}
