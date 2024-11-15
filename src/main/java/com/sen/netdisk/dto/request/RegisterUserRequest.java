package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:35
 */
@Data
@ApiModel
public class RegisterUserRequest {

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "邮箱信息不能为空")
    private String email;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    @Size(min = 8, max = 15, message = "密码长度应该在8-15位之间")
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
