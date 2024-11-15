package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 15:03
 */
@Data
@ApiModel
public class ModifySysSettingRequest {

    @ApiModelProperty
    private String registerEmailTitle;

    @ApiModelProperty
    private String registerEmailContent;

    //单位MB
    @NotNull
    @Min(value = 1, message = "最小为1")
    private Integer userInitTotalSpace;
}
