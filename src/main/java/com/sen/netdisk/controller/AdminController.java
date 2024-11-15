package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.dto.request.ModifySysSettingRequest;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.request.UserListRequest;
import com.sen.netdisk.dto.vo.UserInfoVO;
import com.sen.netdisk.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 23:50
 */
@Api
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @ApiOperation("获取系统设置")
    @GetMapping("/getSysSetting")
    public SenCommonResponse<?> getSysSetting() {
        return SenCommonResponse.success(adminService.getSysSetting());
    }

    @ApiOperation("保存系统设置")
    @PostMapping("/saveSysSetting")
    public SenCommonResponse<?> saveSysSetting(@RequestBody ModifySysSettingRequest request) {
        return SenCommonResponse.success(adminService.saveSysSetting(request));
    }


    @ApiOperation("查询用户列表")
    @PostMapping("/queryUserList")
    public SenCommonResponse<SenCommonPage<UserInfoVO>> queryUserList(@RequestBody UserListRequest request) {
        return SenCommonResponse.success(adminService.queryUserList(request));
    }


    @ApiOperation("更改用户状态")
    @PostMapping("/updateUserStatus")
    public SenCommonResponse<SenCommonPage<UserInfoVO>> updateUserStatus(@RequestParam("userId") String userId,
                                                                         @RequestParam("status") Integer status) {
        adminService.updateUserStatus(userId, status);
        return SenCommonResponse.success();
    }

    @ApiOperation("分配用户空间")
    @PostMapping("/updateUserSpace")
    public SenCommonResponse<SenCommonPage<UserInfoVO>> updateUserSpace(@RequestParam("userId") String userId,
                                                                         @RequestParam("totalSpace") Long totalSpace) {
        adminService.updateUserSpace(userId, totalSpace);
        return SenCommonResponse.success();
    }

}
