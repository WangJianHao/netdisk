package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/25 1:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserListRequest extends PageRequest{

    @ApiModelProperty("昵称模糊搜索")
    private String nickNameFuzzy;

    @ApiModelProperty("状态")
    private Integer status;
}
