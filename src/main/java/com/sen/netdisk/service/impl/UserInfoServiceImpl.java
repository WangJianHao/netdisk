package com.sen.netdisk.service.impl;

import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.constant.Constant;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.component.AppConfig;
import com.sen.netdisk.component.SenUserContextHolder;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.request.UpdatePasswordRequest;
import com.sen.netdisk.dto.vo.UserInfoVO;
import com.sen.netdisk.entity.UserInfoDO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.AccountService;
import com.sen.netdisk.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Objects;


/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:13
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final AccountService accountService;

    private final PasswordEncoder passwordEncoder;

    private final UserInfoDAO userInfoDAO;

    private final RedisCache redisCache;

    private final AppConfig appConfig;

    @Override
    public void getHeadIcon(String userId, HttpServletResponse response) throws BusinessException {
        String headIconFolderPath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_HEAD_ICON_PATH;
        File folder = new File(headIconFolderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new BusinessException("未找到头像所在位置");
        }
        String headIconPath = headIconFolderPath + userId + Constant.HEAD_ICON_SUFFIX;
        File file = new File(headIconPath);
        if (!file.exists()) {
            String defaultHeadIconPath = headIconFolderPath + Constant.HEAD_ICON_DEFAULT;
            File defaultHeadIcon = new File(defaultHeadIconPath);
            if (!defaultHeadIcon.exists()) {
                printNoDefaultImage(response);
            }
            headIconPath = defaultHeadIconPath;
        }

        response.setContentType("image/jpeg");
        readFile(response, headIconPath);
    }

    @Override
    public void updateHeadIcon(MultipartFile multipartFile) throws BusinessException {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String headIconFolderPath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_HEAD_ICON_PATH;
        try {
            File folder = new File(headIconFolderPath);
            if (!folder.exists() && folder.mkdirs()) {
                throw new BusinessException("上传失败");
            }
            String headIconPath = headIconFolderPath + currentUser.getUserId() + Constant.HEAD_ICON_SUFFIX;
            File targetFile = new File(headIconPath);
            multipartFile.transferTo(targetFile);

            UserInfoDO userInfoDO = new UserInfoDO();
            String headICon = currentUser.getUserId() + Constant.HEAD_ICON_SUFFIX;
            userInfoDO.setHeadIcon(headICon);
            userInfoDAO.updateHeadIconByUserId(currentUser.getUserId(), headICon);
        } catch (IOException e) {
            throw new BusinessException("上传文件失败");
        }
    }

    @Override
    public UserSpaceDTO getUserSpace() {
        String userId = SenUserContextHolder.getCurrentUser().getUserId();
        return redisCache.getUserSpaceDTO(userId);
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        return SourceTargetMapper.INSTANCE.convert(SenUserContextHolder.getCurrentUser());
    }

    @Override
    public void updatePassword(UpdatePasswordRequest request, HttpServletRequest servletRequest) throws BusinessException {

        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String oldPassword = request.getPassword();
        String newPassword = request.getNewPassword();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        int count = userInfoDAO.updatePasswordByEmail(currentUser.getEmail(), passwordEncoder.encode(newPassword));
        if (count < 1) {
            throw new BusinessException("密码修改失败");
        }
        //修改密码成功后需要重新登录，因此需要清除当前的token
        accountService.logout(servletRequest);
    }

    private void readFile(HttpServletResponse response, String filePath) throws BusinessException {

        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            fileInputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            byte[] byteData = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(byteData)) != -1) {
                outputStream.write(byteData, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("找不到文件所在位置", e);
            throw new BusinessException("找不到文件所在位置");
        } finally {
            if (Objects.nonNull(outputStream)) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("IO异常", e);
                }
            }
            if (Objects.nonNull(fileInputStream)) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("文件读取完成后发生错误", e);
                }
            }
        }
    }

    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print("请在头像目录下放置默认头像default_avatar.jpg");
            writer.close();
        } catch (Exception e) {
            log.error("输出无默认图失败", e);
        } finally {
            writer.close();
        }
    }
}
