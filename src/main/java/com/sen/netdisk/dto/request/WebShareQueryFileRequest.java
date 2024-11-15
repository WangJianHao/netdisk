package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 23:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WebShareQueryFileRequest extends PageRequest {

    @ApiModelProperty(value = "shareId", required = true)
    @NotEmpty(message = "分享ID不能为空")
    private String shareId;

    @ApiModelProperty("parentId")
    private String parentId;
}
