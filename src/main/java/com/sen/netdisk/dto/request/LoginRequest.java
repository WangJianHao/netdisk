package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:42
 */
@Data
@ApiModel
public class LoginRequest {
    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
