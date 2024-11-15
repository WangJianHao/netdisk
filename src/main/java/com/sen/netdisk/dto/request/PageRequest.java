package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 14:36
 */
@Data
public class PageRequest {

    @ApiModelProperty("当前页码")
    @NotNull
    private Integer current = 1;

    @ApiModelProperty("每页数量")
    @NotNull
    private Integer size = 10;
}
