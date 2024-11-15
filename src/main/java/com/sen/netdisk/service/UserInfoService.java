package com.sen.netdisk.service;

import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.request.UpdatePasswordRequest;
import com.sen.netdisk.dto.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:13
 */
public interface UserInfoService {

    void getHeadIcon(String userId, HttpServletResponse response) throws BusinessException;

    void updateHeadIcon(MultipartFile multipartFile) throws BusinessException;

    UserSpaceDTO getUserSpace();

    UserInfoVO getCurrentUserInfo();

    void updatePassword(UpdatePasswordRequest request, HttpServletRequest servletRequest) throws BusinessException;
}
