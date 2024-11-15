package com.sen.netdisk.dto.vo;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 16:39
 */
@Data
public class FileInfoNode extends FileInfoVO {

    private List<FileInfoNode> children;
}
