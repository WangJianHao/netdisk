package com.sen.netdisk.cache;

import com.sen.netdisk.dto.UserInfoDTO;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/12 3:05
 */
public interface UserInfoCacheService {

    /**
     * 删除后台用户缓存
     */
    void delUser(Long adminId);

    /**
     * 删除后台用户资源列表缓存
     */
    void delResourceList(Long adminId);

    /**
     * 当角色相关资源信息改变时删除相关后台用户缓存
     */
    void delResourceListByRole(Long roleId);

    /**
     * 当角色相关资源信息改变时删除相关后台用户缓存
     */
    void delResourceListByRoleIds(List<Long> roleIds);

    /**
     * 当资源信息改变时，删除资源项目后台用户缓存
     */
    void delResourceListByResource(Long resourceId);

    /**
     * 获取缓存后台用户信息
     */
    UserInfoDTO getUser(String username);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(UserInfoDTO admin);

}
