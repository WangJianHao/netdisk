package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.constant.Constant;
import com.sen.netdisk.common.constant.MQTopicConstant;
import com.sen.netdisk.common.constant.UserStatusEnum;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.SysSettingDTO;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.query.UserInfoQuery;
import com.sen.netdisk.dto.request.ModifySysSettingRequest;
import com.sen.netdisk.dto.request.UserListRequest;
import com.sen.netdisk.dto.vo.UserInfoVO;
import com.sen.netdisk.entity.UserInfoDO;
import com.sen.netdisk.mapper.FileInfoDAO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 15:00
 */
@Service
@Slf4j
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RedisCache redisCache;

    private final UserInfoDAO userInfoDAO;

    private final FileInfoDAO fileInfoDAO;

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public SysSettingDTO getSysSetting() {
        return redisCache.getSysSettingDTO();
    }

    @Override
    public SysSettingDTO saveSysSetting(ModifySysSettingRequest request) {
        SysSettingDTO sysSettingDTO = SourceTargetMapper.INSTANCE.toSysSettingDTO(request);
        redisCache.saveSysSettingDTO(sysSettingDTO);
        return getSysSetting();
    }

    @Override
    public SenCommonPage<UserInfoVO> queryUserList(UserListRequest request) {
        Page<UserInfoDO> page = new Page<>();
        page.setCurrent(request.getCurrent());
        page.setSize(request.getSize());
        UserInfoQuery userInfoQuery = new UserInfoQuery();
        userInfoQuery.setNickNameFuzzy(request.getNickNameFuzzy());
        userInfoQuery.setStatus(request.getStatus());
        page = userInfoDAO.queryPage(page, userInfoQuery);
        IPage<UserInfoVO> convert = page.convert(SourceTargetMapper.INSTANCE::convertToUserInfoVO);
        return SenCommonPage.restPage(convert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        int count = userInfoDAO.updateStatusById(userId, status);
        if (count < 1) {
            throw new BusinessException("更改用户状态失败");
        }
        if (UserStatusEnum.DISABLE.getCode().equals(status)) {
            fileInfoDAO.deleteByUserId(userId);
        }
    }

    @Override
    public void updateUserSpace(String userId, Long totalSpace) {
        Long totalSpaceBit = totalSpace * Constant.MB;
        int count = userInfoDAO.updateSpaceByUserId(userId, null, totalSpaceBit);
        if (count < 1) {
            throw new BusinessException("分配空间失败");
        }

        Long useSpace = userInfoDAO.selectUseSpaceByUserId(userId);
        UserSpaceDTO userSpaceDTO = new UserSpaceDTO();
        userSpaceDTO.setUseSpace(useSpace);
        userSpaceDTO.setTotalSpace(totalSpaceBit);
        redisCache.saveUseSpaceDTO(userId, userSpaceDTO);


        //消息推送，实时修改前端用户空间大小
        rocketMQTemplate.asyncSend(MQTopicConstant.USER_SPACE_TOPIC, userSpaceDTO, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("消息发送成功:{}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("消息发送失败:", throwable);
            }
        });
    }


}
