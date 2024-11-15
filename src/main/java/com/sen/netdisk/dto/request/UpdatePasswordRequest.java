package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 12:24
 */
@Data
public class UpdatePasswordRequest {

    @ApiModelProperty(value = "原密码", required = true)
    @NotBlank(message = "原密码不能为空")
    private String password;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}
