package com.sen.netdisk.cache.impl;

import com.sen.netdisk.cache.UserInfoCacheService;
import com.sen.netdisk.dto.UserInfoDTO;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 13:09
 */
public class UserInfoCacheServiceImpl implements UserInfoCacheService {

    @Override
    public void delUser(Long adminId) {

    }

    @Override
    public void delResourceList(Long adminId) {

    }

    @Override
    public void delResourceListByRole(Long roleId) {

    }

    @Override
    public void delResourceListByRoleIds(List<Long> roleIds) {

    }

    @Override
    public void delResourceListByResource(Long resourceId) {

    }

    @Override
    public UserInfoDTO getUser(String username) {
        return null;
    }

    @Override
    public void setAdmin(UserInfoDTO admin) {

    }
}
