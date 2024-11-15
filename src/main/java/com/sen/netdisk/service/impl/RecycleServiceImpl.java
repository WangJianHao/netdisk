package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.constant.DelFlagEnum;
import com.sen.netdisk.common.constant.FolderTypeEnum;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.common.utils.SnowFlakeIDGenerator;
import com.sen.netdisk.component.SenUserContextHolder;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.query.FileInfoQuery;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.vo.FileInfoNode;
import com.sen.netdisk.dto.vo.RecycleFileInfoVO;
import com.sen.netdisk.entity.FileInfoDO;
import com.sen.netdisk.mapper.FileInfoDAO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.RecycleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 14:28
 */
@Slf4j
@Service
@AllArgsConstructor
public class RecycleServiceImpl implements RecycleService {

    private FileInfoDAO fileInfoDAO;

    private UserInfoDAO userInfoDAO;

    private SnowFlakeIDGenerator snowFlakeIDGenerator;

    private RedisCache redisCache;

    @Override
    public List<FileInfoNode> queryRecycleTree() {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String userId = currentUser.getUserId();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setDelFlag(DelFlagEnum.RECOVERY.getCode());
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
        return fileInfoDOS.stream()
                .filter(fileInfoDO -> StringUtils.equals(fileInfoDO.getParentId(), "0"))
                .map(fileInfoDO -> convertFileInfoNode(fileInfoDO, fileInfoDOS))
                .collect(Collectors.toList());
    }

    @Override
    public SenCommonPage<RecycleFileInfoVO> queryRecycleList(PageRequest pageRequest) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        Page<FileInfoDO> page = new Page<>();
        page.setCurrent(pageRequest.getCurrent());
        page.setSize(pageRequest.getSize());
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(currentUser.getUserId());
        query.setDelFlag(DelFlagEnum.RECOVERY.getCode());
        IPage<FileInfoDO> fileInfoWithPage = fileInfoDAO.listFileInfoWithPage(page, query);
        return SenCommonPage.restPage(fileInfoWithPage.convert(SourceTargetMapper.INSTANCE::toRecycleFileInfoVO));
    }

    @Override
    public void recoverFile(String fileIdS) {
        List<String> fileIdList = Arrays.asList(fileIdS.split(","));

        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(currentUser.getUserId());
        query.setFileIdList(fileIdList);
        query.setDelFlag(DelFlagEnum.RECOVERY.getCode());
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
        if (fileInfoDOS.isEmpty()) {
            return;
        }
        List<FileInfoDO> recoverFieldList = new ArrayList<>();
        fillAllRecoverFileInfo(recoverFieldList, fileInfoDOS);
        if (!recoverFieldList.isEmpty()) {
            fileInfoDAO.updateBatch(recoverFieldList, DelFlagEnum.RECOVERY.getCode());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFile(String fileIds) {
        List<String> fileIdList = Arrays.asList(fileIds.split(","));

        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(currentUser.getUserId());
        query.setFileIdList(fileIdList);
        query.setDelFlag(DelFlagEnum.RECOVERY.getCode());
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
        if (fileInfoDOS.isEmpty()) {
            return;
        }
        List<String> delFieldList = new ArrayList<>();
        getAllFileId(delFieldList, fileInfoDOS);
        if (!delFieldList.isEmpty()) {
            fileInfoDAO.updateDelFlagBatch(delFieldList, currentUser.getUserId(),
                    DelFlagEnum.DELETE.getCode(), Timestamp.valueOf(LocalDateTime.now()), null, DelFlagEnum.RECOVERY.getCode());
        }

        //更改use_space，设置缓存
        Long useSpace = fileInfoDAO.selectUseSpaceByUserId(currentUser.getUserId());
        userInfoDAO.updateUseSpaceByUserId(currentUser.getUserId(), useSpace);
        UserSpaceDTO userSpaceDTO = redisCache.getUserSpaceDTO(currentUser.getUserId());
        userSpaceDTO.setUseSpace(useSpace);
        redisCache.saveUseSpaceDTO(currentUser.getUserId(), userSpaceDTO);
    }

    private void getAllFileId(List<String> delFileIdList, List<FileInfoDO> fileInfoDOS) {
        if (fileInfoDOS == null || fileInfoDOS.isEmpty() || delFileIdList == null) {
            return;
        }
        fileInfoDOS.forEach(fileInfoDO -> {
            delFileIdList.add(fileInfoDO.getFileId());
            if (FolderTypeEnum.FOLDER.getCode().equals(fileInfoDO.getFolderType())) {
                FileInfoQuery query = new FileInfoQuery();
                query.setUserId(fileInfoDO.getUserId());
                query.setParentId(fileInfoDO.getFileId());
                query.setDelFlag(DelFlagEnum.RECOVERY.getCode());
                getAllFileId(delFileIdList, fileInfoDAO.queryList(query));
            }
        });
    }


    private void fillAllRecoverFileInfo(List<FileInfoDO> recoverFieldList, List<FileInfoDO> fileInfoDOS) {
        if (fileInfoDOS == null || fileInfoDOS.isEmpty() || recoverFieldList == null) {
            return;
        }
        fileInfoDOS.forEach(fileInfoDO -> {
            FileInfoDO updateFileInfoDO = SourceTargetMapper.INSTANCE.copy(fileInfoDO);
            FileInfoQuery query = new FileInfoQuery();
            query.setParentId(fileInfoDO.getParentId());
            query.setUserId(fileInfoDO.getUserId());
            query.setFileName(fileInfoDO.getFileName());
            query.setFolderType(fileInfoDO.getFolderType());
            query.setDelFlag(DelFlagEnum.NORMAL.getCode());
            int count = fileInfoDAO.queryCount(query);
            if (count > 0) {
                updateFileInfoDO.setFileName(rename(fileInfoDO.getFileName()));
            }
            updateFileInfoDO.setDelFlag(DelFlagEnum.NORMAL.getCode());
            recoverFieldList.add(updateFileInfoDO);
            if (FolderTypeEnum.FOLDER.getCode().equals(fileInfoDO.getFolderType())) {
                query = new FileInfoQuery();
                query.setUserId(fileInfoDO.getUserId());
                query.setParentId(fileInfoDO.getFileId());
                query.setDelFlag(DelFlagEnum.RECOVERY.getCode());
                fillAllRecoverFileInfo(recoverFieldList, fileInfoDAO.queryList(query));
            }
        });
    }

    private String rename(String fileName) {
        return getFileNameNoSuffix(fileName) + "_" + snowFlakeIDGenerator.nextId() + getFileSuffix(fileName);
    }

    private String getFileNameNoSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    private String getFileSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        fileName = fileName.substring(index);
        return fileName;
    }


    private void checkFileName(String parentId, String userId, String fileName, Integer folderType) {
        FileInfoQuery query = new FileInfoQuery();
        query.setParentId(parentId);
        query.setUserId(userId);
        query.setFileName(fileName);
        query.setFolderType(folderType);
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        int count = fileInfoDAO.queryCount(query);
        if (count > 0) {
            throw new BusinessException("当前目录下已存在同名文件，请修改名称");
        }
    }

    private FileInfoNode convertFileInfoNode(FileInfoDO parentFileInfDO, List<FileInfoDO> fileInfoDOS) {
        FileInfoNode fileInfoNode = SourceTargetMapper.INSTANCE.convert(parentFileInfDO);
        List<FileInfoNode> children = fileInfoDOS.stream()
                .filter(infoDO -> StringUtils.equals(parentFileInfDO.getFileId(), infoDO.getParentId()))
                .map(infoDO -> convertFileInfoNode(infoDO, fileInfoDOS))
                .collect(Collectors.toList());
        fileInfoNode.setChildren(children);
        return fileInfoNode;
    }
}
