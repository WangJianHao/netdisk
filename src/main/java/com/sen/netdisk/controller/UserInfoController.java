package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.component.AppConfig;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.request.UpdatePasswordRequest;
import com.sen.netdisk.dto.vo.UserInfoVO;
import com.sen.netdisk.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 22:35
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api("用户服务")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final AppConfig appConfig;

    public UserInfoController(UserInfoService userInfoService, AppConfig appConfig) {
        this.userInfoService = userInfoService;
        this.appConfig = appConfig;
    }

    @ApiOperation(value = "获取用户头像")
    @GetMapping("/headIcons/{userId}")
    public void getHeadIcon(@PathVariable("userId") String userId, HttpServletResponse response) throws BusinessException {
        userInfoService.getHeadIcon(userId, response);
    }

    @ApiOperation(value = "更新用户头像")
    @PostMapping(value = "/updateHeadIcon")
    @ResponseBody
    public SenCommonResponse<?> getHeadIcon(MultipartFile headIcon) throws BusinessException {
        userInfoService.updateHeadIcon(headIcon);
        return SenCommonResponse.success();
    }

    @ApiOperation(value = "获取用户空间")
    @GetMapping(value = "/getUserSpace")
    @ResponseBody
    public SenCommonResponse<UserSpaceDTO> getUserSpace() {
        return SenCommonResponse.success(userInfoService.getUserSpace());
    }

    @ApiOperation(value = "获取当前用户信息")
    @GetMapping(value = "/getCurrentUserInfo")
    @ResponseBody
    public SenCommonResponse<UserInfoVO> getCurrentUserInfo(HttpSession session) {
        UserInfoVO currentUserInfo = userInfoService.getCurrentUserInfo();
        String id = session.getId();
        session.setAttribute("currentUserId", currentUserInfo.getUserId());
        log.info("login after sessionId:{}", id);
        return SenCommonResponse.success(currentUserInfo);
    }

    /**
     * 修改密码后会退出登录，需要重新登录
     */
    @ApiOperation(value = "修改密码")
    @PostMapping(value = "/updatePassword")
    @ResponseBody
    public SenCommonResponse<?> updatePassword(@RequestBody UpdatePasswordRequest request, HttpServletRequest servletRequest) throws BusinessException {
        userInfoService.updatePassword(request, servletRequest);
        return SenCommonResponse.success();
    }

}
