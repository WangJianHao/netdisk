package com.sen.netdisk.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoadDataListRequest extends PageRequest {

    @ApiModelProperty("文件分类")
    private Integer category;

    @ApiModelProperty("目录ID")
    private String parentId;

    @ApiModelProperty("模糊搜索")
    private String fileNameFuzzy;
}
