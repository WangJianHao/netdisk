package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.constant.*;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.common.utils.DateUtil;
import com.sen.netdisk.common.utils.SnowFlakeIDGenerator;
import com.sen.netdisk.component.SenUserContextHolder;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.FileShareDTO;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.dto.query.ShareQuery;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.vo.FileShareVO;
import com.sen.netdisk.entity.FileInfoDO;
import com.sen.netdisk.entity.ShareInfoDO;
import com.sen.netdisk.mapper.FileInfoDAO;
import com.sen.netdisk.mapper.ShareInfoDAO;
import com.sen.netdisk.service.ShareService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 18:05
 */
@Slf4j
@Service
@AllArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareInfoDAO shareInfoDAO;

    private final FileInfoDAO fileInfoDAO;

    private final SnowFlakeIDGenerator snowFlakeIDGenerator;

    @Override
    public SenCommonPage<FileShareVO> queryShareList(PageRequest request) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        Page<FileShareDTO> page = new Page<>();
        page.setCurrent(request.getCurrent());
        page.setSize(request.getSize());
        ShareQuery shareQuery = new ShareQuery();
        shareQuery.setUserId(currentUser.getUserId());
        Page<FileShareDTO> resPage = shareInfoDAO.selectPage(page, shareQuery);
        return SenCommonPage.restPage(resPage.convert(SourceTargetMapper.INSTANCE::toFileShareVO));
    }

    @Override
    public void test() {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        LambdaQueryWrapper<ShareInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareInfoDO::getUserId, currentUser.getUserId())
                .eq(ShareInfoDO::getShareId, "1");
        ShareInfoDO shareInfoDO = shareInfoDAO.selectOne(queryWrapper);
        System.out.println(shareInfoDO);
        //SELECT share_id,user_id,file_id,valid_type,expire_time,share_time,code,show_count
        // FROM tbas_share
        // WHERE (user_id = ? AND share_id = ?)

    }

    @Override
    public FileShareVO saveShare(String fileId, Integer validType, String code) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        ShareValidTypeEnum enumByCode = ShareValidTypeEnum.getEnumByCode(validType);
        if (Objects.isNull(enumByCode)) {
            throw new BusinessException("参数错误");
        }
        if (StringUtils.equals(Constant.ZERO_STR, fileId)) {
            throw new BusinessException("参数错误");
        }
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, currentUser.getUserId());
        if (Objects.isNull(fileInfoDO)) {
            throw new BusinessException("文件不存在，无法创建分享链接");
        }
        if (FolderTypeEnum.FILE.getCode().equals(fileInfoDO.getFolderType())) {
            if (!FileStatusEnum.CODE_SUCCESS.getCode().equals(fileInfoDO.getStatus())) {
                throw new BusinessException("请选择转码成功的文件");
            }
            if (!DelFlagEnum.NORMAL.getCode().equals(fileInfoDO.getDelFlag())) {
                throw new BusinessException("请选择使用中的文件");
            }
        }


        ShareInfoDO shareInfoDO = new ShareInfoDO();
        if (ShareValidTypeEnum.FOREVER != enumByCode) {
            shareInfoDO.setExpireTime(DateUtil.getNextDay(LocalDateTime.now(), enumByCode.getDay()));
        }
        shareInfoDO.setUserId(currentUser.getUserId());
        shareInfoDO.setFileId(fileId);
        shareInfoDO.setValidType(validType);
        shareInfoDO.setShareId(String.valueOf(snowFlakeIDGenerator.nextId()));
        shareInfoDO.setShareTime(Timestamp.valueOf(LocalDateTime.now()));
        shareInfoDO.setShowCount(Constant.ZERO);
        if (StringUtils.isEmpty(code)) {
            //提取码默认四位
            shareInfoDO.setCode(UUID.randomUUID().toString().substring(0, 4));
        } else {
            shareInfoDO.setCode(code);
        }
        int count = shareInfoDAO.insert(shareInfoDO);
        if (count < 1) {
            throw new BusinessException("分享失败");
        }

        return SourceTargetMapper.INSTANCE.toFileShareVO(shareInfoDAO.selectByShareId(shareInfoDO.getShareId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShare(String shareIds) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        List<String> shareIdList = Arrays.asList(shareIds.split(","));
        int count = shareInfoDAO.deleteBatch(currentUser.getUserId(), shareIdList);
        if (count != shareIdList.size()) {
            throw new BusinessException("取消分享失败");
        }
    }

}
