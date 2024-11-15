package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 22:03
 */
@ApiModel("修改密码")
@Data
public class ResetPasswordRequest {

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "重置后的新密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 图片验证码
     */
    @ApiModelProperty(value = "图片验证码")
    @NotBlank(message = "验证码不能为空")
    private String authCode;

    /**
     * 邮箱验证码
     */
    @ApiModelProperty(value = "邮箱验证码")
    @NotBlank(message = "验证码不能为空")
    private String emailCode;

}
