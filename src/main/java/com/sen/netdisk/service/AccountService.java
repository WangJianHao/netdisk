package com.sen.netdisk.service;

import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.dto.request.LoginRequest;
import com.sen.netdisk.dto.request.RegisterUserRequest;
import com.sen.netdisk.dto.request.ResetPasswordRequest;
import com.sen.netdisk.dto.response.LoginResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 15:39
 */
public interface AccountService {


    Boolean verifyAuthCode(String mail, String authCode) throws BusinessException;

    void generateMailAuthCode(String email) throws BusinessException;

    void register(RegisterUserRequest request) throws BusinessException;

    LoginResponse login(LoginRequest request, HttpSession session);

    void resetPassword(ResetPasswordRequest request) throws BusinessException;

    void logout(HttpServletRequest request);

}
