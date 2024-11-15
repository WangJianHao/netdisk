package com.sen.netdisk.cache;

import com.sen.netdisk.common.constant.Constant;
import com.sen.netdisk.dto.DownLoadFileDTO;
import com.sen.netdisk.dto.SysSettingDTO;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.mapper.FileInfoDAO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.RedisService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/9 0:08
 */
@Component
public class RedisCache {

    private final RedisService<Object> redisService;

    private final UserInfoDAO userInfoDAO;

    private final FileInfoDAO fileInfoDAO;

    public RedisCache(RedisService<Object> redisService, UserInfoDAO userInfoDAO, FileInfoDAO fileInfoDAO) {
        this.redisService = redisService;
        this.userInfoDAO = userInfoDAO;
        this.fileInfoDAO = fileInfoDAO;
    }

    public SysSettingDTO getSysSettingDTO() {
        SysSettingDTO sysSettingDTO = (SysSettingDTO) redisService.get(Constant.SYS_SETTING_KEY);
        if (Objects.isNull(sysSettingDTO)) {
            sysSettingDTO = new SysSettingDTO();
            redisService.set(Constant.SYS_SETTING_KEY, sysSettingDTO);
        }
        return sysSettingDTO;
    }

    public void saveSysSettingDTO(SysSettingDTO sysSettingDTO) {
        redisService.set(Constant.SYS_SETTING_KEY, sysSettingDTO);
    }

    public UserSpaceDTO getUserSpaceDTO(String userId) {
        String userSpaceKey = Constant.USER_SPACE_KEY + userId;
        UserSpaceDTO userSpaceDTO = (UserSpaceDTO) redisService.get(userSpaceKey);
        if (Objects.isNull(userSpaceDTO)) {
            userSpaceDTO = new UserSpaceDTO();
            userSpaceDTO.setTotalSpace(getSysSettingDTO().getUserInitTotalSpace() * Constant.MB);
            Long useSpace = userInfoDAO.selectUseSpaceByUserId(userId);
            userSpaceDTO.setUseSpace(useSpace);
            redisService.set(userSpaceKey, userSpaceDTO);
        }
        return userSpaceDTO;
    }

    public void saveUseSpaceDTO(String userId, UserSpaceDTO userSpaceDTO) {
        String userSpaceKey = Constant.USER_SPACE_KEY + userId;
        redisService.set(userSpaceKey, userSpaceDTO);
    }


    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        redisService.setEx(Constant.TEMP_FILE_KEY + userId + fileId, currentSize + fileSize, Constant.ONE_HOUR);
    }


    //获取临时文件大小
    public Long getFileTempSize(String userId, String fileId) {
        return getFileSizeFromRedis(Constant.TEMP_FILE_KEY + userId + fileId);
    }

    private Long getFileSizeFromRedis(String key) {
        Object sizeObj = redisService.get(key);
        if (Objects.isNull(sizeObj)) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }
        return 0L;
    }

    public void saveDownloadCode(DownLoadFileDTO downLoadFileDTO) {
        redisService.setEx(Constant.DOWNLOAD_CODE_KEY + downLoadFileDTO.getDownloadCode(), downLoadFileDTO, Constant.TEN_MINUTE);
    }

    public DownLoadFileDTO getDownloadCode(String downloadCode) {
        return (DownLoadFileDTO) redisService.get(Constant.DOWNLOAD_CODE_KEY + downloadCode);
    }

    public void reloadUserSpace(List<String> userIdList) {
        for (String userId : userIdList) {
            UserSpaceDTO userSpaceDTO = userInfoDAO.selectSpaceByUserId(userId);
            saveUseSpaceDTO(userId, userSpaceDTO);
        }
    }


}
