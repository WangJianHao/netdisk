package com.sen.netdisk.dto;

import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 12:08
 */
@Data
public class DownLoadFileDTO {

    private String downloadCode;

    private String fileId;

    private String fileName;

    private String filePath;
}
