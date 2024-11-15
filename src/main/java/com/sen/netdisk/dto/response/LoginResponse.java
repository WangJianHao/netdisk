package com.sen.netdisk.dto.response;

import com.sen.netdisk.dto.vo.UserInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/10 18:51
 */
@Data
public class LoginResponse {

    @ApiModelProperty("用户信息")
    private UserInfoVO userInfoVO;

    @ApiModelProperty("用户token")
    private String token;
}
