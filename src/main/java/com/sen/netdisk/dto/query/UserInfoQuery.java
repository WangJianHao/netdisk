package com.sen.netdisk.dto.query;

import lombok.Data;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/25 1:59
 */
@Data
public class UserInfoQuery {

    private String nickNameFuzzy;

    private Integer status;
}
