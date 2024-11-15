package com.sen.netdisk.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 2:48
 */
@Data
public class UserSpaceDTO implements Serializable {

    /**
     * 使用空间
     */
    private Long useSpace;

    /**
     * 总空间
     */
    private Long totalSpace;
}
