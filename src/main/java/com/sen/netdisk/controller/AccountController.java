package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.common.utils.DateUtil;
import com.sen.netdisk.dto.ImageCode;
import com.sen.netdisk.dto.request.LoginRequest;
import com.sen.netdisk.dto.request.RegisterUserRequest;
import com.sen.netdisk.dto.request.ResetPasswordRequest;
import com.sen.netdisk.dto.response.LoginResponse;
import com.sen.netdisk.service.AccountService;
import com.sen.netdisk.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:16
 */
@Api("账号服务")
@Slf4j
@Controller
@RequestMapping("/account")
@Valid
public class AccountController {

    private final UserInfoService userInfoService;

    private final AccountService accountService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    public AccountController(UserInfoService userInfoService, AccountService accountService) {
        this.userInfoService = userInfoService;
        this.accountService = accountService;
    }

    @ApiOperation(value = "获取图片验证码")
    @GetMapping(value = "/getCaptureCode")
    @ResponseBody
    public void getCaptureCode(HttpServletResponse response, HttpSession session) {
        ImageCode imageCode = new ImageCode();
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        String code = imageCode.getCode();
        try {
            imageCode.write(response.getOutputStream());
            session.setAttribute(ImageCode.IMAGE_CODE_KEY, code);
            session.setAttribute(ImageCode.IMAGE_CODE_DATE, DateUtil.getCurrentTimestamp());
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation("验证图片验证码是否正确")
    @GetMapping(value = "/verifyCaptureCode")
    @ResponseBody
    public SenCommonResponse<Boolean> verifyCaptureCode(@RequestParam(name = "captureCode") String captureCode, HttpSession session) throws IOException {
        if (StringUtils.isBlank(captureCode)) {
            return SenCommonResponse.success(false);
        }
        String code = (String) session.getAttribute(ImageCode.IMAGE_CODE_KEY);
        Long createTimestamp = (Long) session.getAttribute(ImageCode.IMAGE_CODE_DATE);
        if (Objects.isNull(code) || Objects.isNull(createTimestamp)) {
            return SenCommonResponse.success(false);
        }
        if (DateUtil.getCurrentTimestamp() - createTimestamp > ImageCode.EXPIRE_TIME) {
            return SenCommonResponse.success(false);
        }
        if (captureCode.equalsIgnoreCase(code)) {
            return SenCommonResponse.success(true);
        }
        return SenCommonResponse.success(false);
    }

    @ApiOperation("判断邮箱验证码是否正确")
    @GetMapping(value = "/verifyAuthCode")
    @ResponseBody
    public SenCommonResponse<Boolean> verifyAuthCode(@RequestParam String mail,
                                                     @RequestParam String authCode) throws BusinessException {
        return SenCommonResponse.success(accountService.verifyAuthCode(mail, authCode));
    }

    @ApiOperation("发送邮箱验证码")
    @PostMapping(value = "/sendMailAuthCode")
    @ResponseBody
    public SenCommonResponse<?> sendMailAuthCode(@RequestParam(name = "email") String email) throws BusinessException {
        accountService.generateMailAuthCode(email);
        return SenCommonResponse.success();
    }

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    @ResponseBody
    public SenCommonResponse<?> register(@RequestBody RegisterUserRequest request, HttpSession session) throws BusinessException {
        if (!request.getAuthCode().equalsIgnoreCase((String) session.getAttribute(ImageCode.IMAGE_CODE_KEY))) {
            throw new BusinessException("图片验证码不正确");
        }
        accountService.register(request);
        return SenCommonResponse.success();
    }

    @ApiOperation(value = "登录以后返回token")
    @PostMapping(value = "/login")
    @ResponseBody
    public SenCommonResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        return SenCommonResponse.success(accountService.login(request, session));
    }

    @ApiOperation(value = "退出")
    @PostMapping(value = "/logout")
    @ResponseBody
    public SenCommonResponse<?> logout(HttpServletRequest request,HttpSession session) {
        accountService.logout(request);
        session.removeAttribute("currentUserId");
        return SenCommonResponse.success();
    }

    /**
     * 忘记密码执行的操作，用户未登录
     */
    @ApiOperation(value = "重置密码")
    @PostMapping(value = "/resetPassword")
    @ResponseBody
    public SenCommonResponse<?> resetPassword(@RequestBody ResetPasswordRequest request, HttpSession session) throws BusinessException {
        if (!request.getAuthCode().equalsIgnoreCase((String) session.getAttribute(ImageCode.IMAGE_CODE_KEY))) {
            throw new BusinessException("图片验证码不正确");
        }
        accountService.resetPassword(request);
        return SenCommonResponse.success();
    }

}
