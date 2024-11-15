package com.sen.netdisk.converter;


import com.sen.netdisk.dto.FileShareDTO;
import com.sen.netdisk.dto.SysSettingDTO;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.dto.request.ModifySysSettingRequest;
import com.sen.netdisk.dto.request.RegisterUserRequest;
import com.sen.netdisk.dto.vo.*;
import com.sen.netdisk.entity.FileInfoDO;
import com.sen.netdisk.entity.ShareInfoDO;
import com.sen.netdisk.entity.UserInfoDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Author:  sensen
 * Date:  2024/7/28 19:39
 */
@Mapper
public interface SourceTargetMapper {

    SourceTargetMapper INSTANCE = Mappers.getMapper(SourceTargetMapper.class);

    UserInfoDO convert(RegisterUserRequest request);

    UserInfoDTO convert(UserInfoDO userInfoDO);

    @Mapping(target = "createTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(userInfoDO.getCreateTime()))")
    @Mapping(target = "lastLoginTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(userInfoDO.getLastLoginTime()))")
    UserInfoVO convertToUserInfoVO(UserInfoDO userInfoDO);

    @Mapping(target = "createTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(userInfoDTO.getCreateTime()))")
    @Mapping(target = "lastLoginTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(userInfoDTO.getLastLoginTime()))")
    UserInfoVO convert(UserInfoDTO userInfoDTO);

    @Mapping(target = "createTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileInfoDO.getCreateTime()))")
    @Mapping(target = "updateTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileInfoDO.getUpdateTime()))")
    FileInfoVO toFileInfoVO(FileInfoDO fileInfoDO);

    @Mapping(target = "createTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileInfoDO.getCreateTime()))")
    @Mapping(target = "updateTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileInfoDO.getUpdateTime()))")
    FileInfoNode convert(FileInfoDO fileInfoDO);

    @Mapping(target = "expireTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(shareInfoDO.getExpireTime()))")
    @Mapping(target = "shareTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(shareInfoDO.getShareTime()))")
    FileShareVO toFileShareVO(ShareInfoDO shareInfoDO);

    @Mapping(target = "expireTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileShareDTO.getExpireTime()))")
    @Mapping(target = "shareTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileShareDTO.getShareTime()))")
    FileShareVO toFileShareVO(FileShareDTO fileShareDTO);

    SysSettingDTO toSysSettingDTO(ModifySysSettingRequest request);

    @Mapping(target = "recoveryTime", expression = "java(com.sen.netdisk.common.utils.DateUtil.formatTime(fileInfoDO.getRecoveryTime()))")
    RecycleFileInfoVO toRecycleFileInfoVO(FileInfoDO fileInfoDO);

    FileInfoDO copy(FileInfoDO fileInfoDO);
}
