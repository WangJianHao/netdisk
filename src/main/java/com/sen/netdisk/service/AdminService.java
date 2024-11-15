package com.sen.netdisk.service;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.dto.SysSettingDTO;
import com.sen.netdisk.dto.request.ModifySysSettingRequest;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.request.UserListRequest;
import com.sen.netdisk.dto.vo.UserInfoVO;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 15:00
 */
public interface AdminService {

    SysSettingDTO getSysSetting();

    SysSettingDTO saveSysSetting(ModifySysSettingRequest request);

    SenCommonPage<UserInfoVO> queryUserList(UserListRequest request);

    void updateUserStatus(String userId, Integer status);

    void updateUserSpace(String userId, Long totalSpace);
}
