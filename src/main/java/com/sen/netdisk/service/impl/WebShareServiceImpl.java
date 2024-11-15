package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.constant.Constant;
import com.sen.netdisk.common.constant.DelFlagEnum;
import com.sen.netdisk.common.constant.FileStatusEnum;
import com.sen.netdisk.common.constant.FolderTypeEnum;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.common.utils.DateUtil;
import com.sen.netdisk.common.utils.SnowFlakeIDGenerator;
import com.sen.netdisk.component.SenUserContextHolder;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.query.FileInfoQuery;
import com.sen.netdisk.dto.request.WebShareQueryFileRequest;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.dto.vo.WebShareVO;
import com.sen.netdisk.entity.FileInfoDO;
import com.sen.netdisk.entity.ShareInfoDO;
import com.sen.netdisk.entity.UserInfoDO;
import com.sen.netdisk.mapper.FileInfoDAO;
import com.sen.netdisk.mapper.ShareInfoDAO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.FileInfoService;
import com.sen.netdisk.service.WebShareService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 16:29
 */
@Slf4j
@Service
@AllArgsConstructor
public class WebShareServiceImpl implements WebShareService {

    private final ShareInfoDAO shareInfoDAO;

    private final FileInfoDAO fileInfoDAO;

    private final UserInfoDAO userInfoDAO;

    private final FileInfoService fileInfoService;

    private final SnowFlakeIDGenerator snowFlakeIDGenerator;

    private final RedisCache redisCache;

    @Override
    public WebShareVO getShareInfo(String shareId) {
        return getShare(shareId);
    }

    @Override
    public WebShareVO getShareInfoAfterAuth(String shareId) {
        return getShare(shareId);
    }

    @Override
    public Boolean checkShareCode(String shareId, String code) {
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        if (Objects.isNull(shareInfoDO)) {
            throw new BusinessException("分享链接不存在");
        }
        String shareCode = shareInfoDO.getCode();
        if (!StringUtils.equals(shareCode, code)) {
            return false;
        }
        //更新浏览次数
        shareInfoDAO.updateShareCount(shareId);
        return true;
    }

    @Override
    public SenCommonPage<FileInfoVO> queryFileList(WebShareQueryFileRequest request) {
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(request.getShareId());
        if (Objects.isNull(shareInfoDO)) {
            throw new BusinessException("分享链接不存在");
        }
        Timestamp expireTime = shareInfoDO.getExpireTime();
        if (expireTime != null && LocalDateTime.now().isAfter(expireTime.toLocalDateTime())) {
            throw new BusinessException("分享链接已过期");
        }
        String shareUserId = shareInfoDO.getUserId();
        String fileId = shareInfoDO.getFileId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, shareUserId);
        if (Objects.isNull(fileInfoDO) || !DelFlagEnum.NORMAL.getCode().equals(fileInfoDO.getDelFlag())) {
            throw new BusinessException("分享文件已被删除");
        }
        UserInfoDO userInfoDO = userInfoDAO.selectByUserId(shareUserId);
        if (Objects.isNull(userInfoDO)) {
            throw new BusinessException("分享用户不存在");
        }
        String parentId = request.getParentId();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(shareUserId);
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        query.setStatus(FileStatusEnum.CODE_SUCCESS.getCode());
        if (!StringUtils.isEmpty(parentId) && !StringUtils.equals(Constant.ZERO_STR, parentId)) {
            //如果存在目录  校验该目录ID是否在分享的目录ID之下
            checkShareFolder(shareUserId, fileInfoDO, parentId);
            query.setParentId(parentId);
        } else {
            //其他情况就是查的分享的根目录或者分享的文件
            SenCommonPage<FileInfoVO> page = new SenCommonPage<>();
            page.setCurrent(request.getCurrent());
            page.setSize(request.getSize());
            page.setTotalPage(1L);
            page.setTotal(1);
            page.setPages(1);
            page.setRecords(Collections.singletonList(SourceTargetMapper.INSTANCE.toFileInfoVO(fileInfoDO)));
            return page;
        }
        Page<FileInfoDO> page = new Page<>();
        page.setCurrent(request.getCurrent());
        page.setSize(request.getSize());
        IPage<FileInfoDO> fileInfoDOIPage = fileInfoDAO.listFileInfoWithPage(page, query);
        return SenCommonPage.restPage(fileInfoDOIPage.convert(SourceTargetMapper.INSTANCE::toFileInfoVO));
    }

    @Override
    public List<FileInfoVO> getFolderInfo(String shareId, String path) {
        checkShare(shareId);
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        String shareUserId = shareInfoDO.getUserId();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(shareUserId);
        query.setFileIdList(Arrays.asList(path.split("/")));
        query.setFolderType(FolderTypeEnum.FOLDER.getCode());
        return fileInfoDAO.queryListSort(query).stream().map(SourceTargetMapper.INSTANCE::toFileInfoVO).collect(Collectors.toList());
    }

    @Override
    public void getFile(String shareId, String fileId, HttpServletResponse response) {
        checkShare(shareId);
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        String shareUserId = shareInfoDO.getUserId();
        String shareFileId = shareInfoDO.getFileId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(shareFileId, shareUserId);
        checkShareFile(shareUserId, fileInfoDO, fileId);
        fileInfoService.getFile(fileId, shareUserId, response);
    }

    @Override
    public void getVideoInfo(String shareId, String fileId, String videoPath, HttpServletResponse response) {
        checkShare(shareId);
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        String shareUserId = shareInfoDO.getUserId();
        String shareFileId = shareInfoDO.getFileId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(shareFileId, shareUserId);
        if (StringUtils.isNotEmpty(fileId) && !fileId.endsWith(".ts")) {
            checkShareFile(shareUserId, fileInfoDO, fileId);
        }
        fileInfoService.getVideoInfo(fileId, videoPath, shareUserId, response);
    }

    @Override
    public String createDownloadURL(String shareId, String fileId) {
        checkShare(shareId);
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        String shareUserId = shareInfoDO.getUserId();
        String shareFileId = shareInfoDO.getFileId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(shareFileId, shareUserId);
        checkShareFile(shareUserId, fileInfoDO, fileId);
        return fileInfoService.createDownloadURL(fileId, shareUserId);
    }

    @Override
    public void downloadFile(String code, HttpServletResponse response) {
        fileInfoService.downloadFile(code, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveShare(String shareId, String shareFileIds, String targetFolderId) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            throw new BusinessException("当前状态未登录");
        }
        if (StringUtils.isEmpty(targetFolderId)) {
            throw new BusinessException("参数错误");
        }
        if (!StringUtils.equals(targetFolderId, Constant.ZERO_STR)) {
            FileInfoDO targetFolderInfo = fileInfoDAO.selectByFileIdAndUserId(targetFolderId, currentUser.getUserId());
            if (Objects.isNull(targetFolderInfo) || !FolderTypeEnum.FOLDER.getCode().equals(targetFolderInfo.getFolderType())) {
                throw new BusinessException("参数错误");
            }
        }

        checkShare(shareId);
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        String shareUserId = shareInfoDO.getUserId();
        String shareFileId = shareInfoDO.getFileId();
        FileInfoDO shareFileInfo = fileInfoDAO.selectByFileIdAndUserId(shareFileId, shareUserId);
        List<String> shareFileIdList = Arrays.asList(shareFileIds.split("/"));
        for (String fileId : shareFileIdList) {
            checkShareFile(shareUserId, shareFileInfo, fileId);
        }
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(shareUserId);
        query.setFileIdList(shareFileIdList);
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        query.setStatus(FileStatusEnum.CODE_SUCCESS.getCode());
        //该层级的文件是要放到targetFolder下的
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
        if (CollectionUtils.isEmpty(fileInfoDOS)) {
            throw new BusinessException("参数错误");
        }
        List<FileInfoDO> destFileInfoList = new ArrayList<>();
        AtomicReference<Long> totalFileSize = new AtomicReference<>();
        totalFileSize.set(0L);
        convertShareFileToSave(currentUser.getUserId(), destFileInfoList, fileInfoDOS, targetFolderId, totalFileSize);

        //用户空间大小判断
        UserSpaceDTO userSpaceDTO = redisCache.getUserSpaceDTO(currentUser.getUserId());
        //比当前剩余空间大
        if (totalFileSize.get().compareTo(userSpaceDTO.getTotalSpace() - userSpaceDTO.getUseSpace()) > 0) {
            throw new BusinessException("剩余空间不足");
        }

        fileInfoDAO.insertBatch(destFileInfoList);
    }

    @Override
    public void getCover(String cover, HttpServletResponse response) {
        fileInfoService.getCover(cover, response);
    }

    /**
     * 将分享文件列表转化为当前用户的文件，需要重新生成ID等等
     *
     * @param destFileInfoList 要保存到数据库的文件列表
     * @param fileInfoDOS      当前层级的分享文件列表
     */
    private void convertShareFileToSave(String currentUserId, List<FileInfoDO> destFileInfoList, List<FileInfoDO> fileInfoDOS, String targetParentId, AtomicReference<Long> totalFileSize) {
        if (fileInfoDOS == null || fileInfoDOS.isEmpty() || destFileInfoList == null) {
            return;
        }
        fileInfoDOS.forEach(srcFileInfo -> {
            FileInfoDO targetFileInfo = new FileInfoDO();
            targetFileInfo.setFileId(String.valueOf(snowFlakeIDGenerator.nextId()));
            targetFileInfo.setUserId(currentUserId);
            targetFileInfo.setParentId(targetParentId);
            targetFileInfo.setFileName(srcFileInfo.getFileName());
            targetFileInfo.setFileSize(srcFileInfo.getFileSize());
            targetFileInfo.setFileCover(srcFileInfo.getFileCover());
            targetFileInfo.setStatus(srcFileInfo.getStatus());
            targetFileInfo.setDelFlag(srcFileInfo.getDelFlag());
            targetFileInfo.setFilePath(srcFileInfo.getFilePath());
            targetFileInfo.setFileCategory(srcFileInfo.getFileCategory());
            targetFileInfo.setMd5(srcFileInfo.getMd5());
            targetFileInfo.setFileType(srcFileInfo.getFileType());
            totalFileSize.getAndUpdate(value -> value + targetFileInfo.getFileSize());
            destFileInfoList.add(targetFileInfo);
            if (FolderTypeEnum.FOLDER.getCode().equals(srcFileInfo.getFolderType())) {
                FileInfoQuery query = new FileInfoQuery();
                query.setUserId(srcFileInfo.getUserId());
                query.setParentId(srcFileInfo.getFileId());
                query.setDelFlag(DelFlagEnum.NORMAL.getCode());
                query.setStatus(FileStatusEnum.CODE_SUCCESS.getCode());
                //下一层级的文件的parentId是这一层目录新生成的这个fileId
                convertShareFileToSave(currentUserId, destFileInfoList, fileInfoDAO.queryList(query), targetFileInfo.getFileId(), totalFileSize);
            }
        });
    }


    private void checkShareFile(String shareUserId, FileInfoDO fileInfoDO, String fileId) {
        if (StringUtils.equals(fileInfoDO.getFileId(), fileId)) {
            return;
        }
        if (!FolderTypeEnum.FOLDER.getCode().equals(fileInfoDO.getFolderType())) {
            throw new BusinessException("参数异常");
        }
        FileInfoDO currentFile = fileInfoDAO.selectByFileIdAndUserId(fileId, shareUserId);
        if (Objects.isNull(currentFile)) {
            throw new BusinessException("参数异常");
        }
        if (StringUtils.equals(currentFile.getParentId(), Constant.ZERO_STR)) {
            throw new BusinessException("参数异常");
        }
        checkShareFile(shareUserId, fileInfoDO, currentFile.getParentId());
    }

    /**
     * 校验传入的目录ID是否在分享的目录ID之下
     *
     * @param shareUserId 分享的用户ID
     * @param fileInfoDO  分享的文件信息
     * @param parentId    前端传入的当前目录ID
     */
    private void checkShareFolder(String shareUserId, FileInfoDO fileInfoDO, String parentId) {
        if (StringUtils.equals(fileInfoDO.getFileId(), parentId)) {
            return;
        }
        if (!FolderTypeEnum.FOLDER.getCode().equals(fileInfoDO.getFolderType())) {
            throw new BusinessException("参数异常");
        }
        FileInfoDO currentFile = fileInfoDAO.selectByFileIdAndUserId(parentId, shareUserId);
        if (Objects.isNull(currentFile)) {
            throw new BusinessException("参数异常");
        }
        if (!FolderTypeEnum.FOLDER.getCode().equals(currentFile.getFolderType())) {
            throw new BusinessException("参数异常");
        }
        if (StringUtils.equals(currentFile.getParentId(), Constant.ZERO_STR)) {
            throw new BusinessException("参数异常");
        }
        checkShareFolder(shareUserId, fileInfoDO, currentFile.getParentId());
    }

    private WebShareVO getShare(String shareId) {
        checkShare(shareId);
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        String shareUserId = shareInfoDO.getUserId();
        String fileId = shareInfoDO.getFileId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, shareUserId);
        UserInfoDO userInfoDO = userInfoDAO.selectByUserId(shareUserId);

        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        WebShareVO webShareVO = new WebShareVO();
        webShareVO.setUserId(shareUserId);
        webShareVO.setNickName(userInfoDO.getNickName());
        webShareVO.setHeadIcon(userInfoDO.getHeadIcon());
        webShareVO.setFileId(fileId);
        webShareVO.setFileName(fileInfoDO.getFileName());
        webShareVO.setShareTime(DateUtil.formatTime(shareInfoDO.getShareTime()));
        webShareVO.setExpireTime(DateUtil.formatTime(shareInfoDO.getExpireTime()));
        if (Objects.isNull(currentUser)) {
            webShareVO.setIsCurrentUser(false);
        } else {
            webShareVO.setIsCurrentUser(StringUtils.equals(currentUser.getUserId(), shareUserId));
        }
        return webShareVO;
    }

    private void checkShare(String shareId) {
        ShareInfoDO shareInfoDO = shareInfoDAO.selectByShareId(shareId);
        if (Objects.isNull(shareInfoDO)) {
            throw new BusinessException("分享链接不存在");
        }
        Timestamp expireTime = shareInfoDO.getExpireTime();
        if (expireTime != null && LocalDateTime.now().isAfter(expireTime.toLocalDateTime())) {
            throw new BusinessException("分享链接已过期");
        }
        String shareUserId = shareInfoDO.getUserId();
        String fileId = shareInfoDO.getFileId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, shareUserId);
        if (Objects.isNull(fileInfoDO) || !DelFlagEnum.NORMAL.getCode().equals(fileInfoDO.getDelFlag())) {
            throw new BusinessException("分享文件已被删除");
        }
        UserInfoDO userInfoDO = userInfoDAO.selectByUserId(shareUserId);
        if (Objects.isNull(userInfoDO)) {
            throw new BusinessException("分享用户不存在");
        }
    }

}
