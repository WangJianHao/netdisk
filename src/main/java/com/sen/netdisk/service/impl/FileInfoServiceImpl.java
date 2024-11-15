package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.constant.*;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.common.utils.DateUtil;
import com.sen.netdisk.common.utils.SnowFlakeIDGenerator;
import com.sen.netdisk.component.AppConfig;
import com.sen.netdisk.component.SenUserContextHolder;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.DownLoadFileDTO;
import com.sen.netdisk.dto.UploadFileResultDTO;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.query.FileInfoQuery;
import com.sen.netdisk.dto.request.LoadDataListRequest;
import com.sen.netdisk.dto.vo.FileInfoNode;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.entity.FileInfoDO;
import com.sen.netdisk.mapper.FileInfoDAO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.FileInfoService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:10
 */
@Service
public class FileInfoServiceImpl implements FileInfoService {

    private static final Logger log = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    private final FileInfoDAO fileInfoDAO;

    private final UserInfoDAO userInfoDAO;

    private final SnowFlakeIDGenerator snowFlakeIDGenerator;

    private final RedisCache redisCache;

    private final AppConfig appConfig;

    private final ThreadPoolTaskExecutor asyncServiceExecutor;

    private final RocketMQTemplate rocketMQTemplate;

    public FileInfoServiceImpl(FileInfoDAO fileInfoDAO, UserInfoDAO userInfoDAO, SnowFlakeIDGenerator snowFlakeIDGenerator,
                               RedisCache redisCache, AppConfig appConfig, ThreadPoolTaskExecutor asyncServiceExecutor, RocketMQTemplate rocketMQTemplate) {
        this.fileInfoDAO = fileInfoDAO;
        this.userInfoDAO = userInfoDAO;
        this.snowFlakeIDGenerator = snowFlakeIDGenerator;
        this.redisCache = redisCache;
        this.appConfig = appConfig;
        this.asyncServiceExecutor = asyncServiceExecutor;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public SenCommonPage<FileInfoVO> listFileInfoWithPage(LoadDataListRequest request) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String userId = currentUser.getUserId();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setFileCategory(request.getCategory());
        query.setParentId(request.getParentId());
        query.setFileNameFuzzy(request.getFileNameFuzzy());
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());

        Page<FileInfoDO> page = new Page<>();
        page.setCurrent(request.getCurrent());
        page.setSize(request.getSize());
        IPage<FileInfoDO> fileInfoDOPage = fileInfoDAO.listFileInfoWithPage(page, query);

        return SenCommonPage.restPage(fileInfoDOPage.convert(SourceTargetMapper.INSTANCE::toFileInfoVO));
    }

    @Override
    public List<FileInfoNode> listFileInfo() {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String userId = currentUser.getUserId();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
        return fileInfoDOS.stream()
                .filter(fileInfoDO -> StringUtils.equals(fileInfoDO.getParentId(), "0"))
                .map(fileInfoDO -> convertFileInfoNode(fileInfoDO, fileInfoDOS))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadFileResultDTO uploadFile(String fileId, MultipartFile file, String fileName, String parentId, String md5,
                                          Integer chunkIndex, Integer chunks) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String userId = currentUser.getUserId();
        UploadFileResultDTO resultDTO = new UploadFileResultDTO();
        if (StringUtils.isEmpty(fileId)) {
            fileId = String.valueOf(snowFlakeIDGenerator.nextId());
        }
        resultDTO.setFileId(fileId);
        UserSpaceDTO userSpaceDTO = redisCache.getUserSpaceDTO(userId);

        boolean uploadSuccess = true;
        File tempFolder = null;
        try {
            if (0 == chunkIndex) {
                FileInfoQuery query = new FileInfoQuery();
                query.setMd5(md5);
                query.setStatus(FileStatusEnum.CODE_SUCCESS.getCode());
                List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
                if (!fileInfoDOS.isEmpty()) {
                    FileInfoDO dbFile = fileInfoDOS.get(0);
                    //判断文件大小
                    if (dbFile.getFileSize() + userSpaceDTO.getUseSpace() > userSpaceDTO.getTotalSpace()) {
                        throw new BusinessException("文件大小超出剩余空间");
                    }
                    dbFile.setFileId(fileId);
                    dbFile.setParentId(parentId);
                    dbFile.setUserId(userId);
                    dbFile.setStatus(FileStatusEnum.CODE_SUCCESS.getCode());
                    dbFile.setDelFlag(DelFlagEnum.NORMAL.getCode());
                    dbFile.setMd5(md5);

                    //文件名重复 重命名
                    fileName = autoRename(parentId, userId, fileName);
                    dbFile.setFileName(fileName);
                    resultDTO.setUploadingStatus(UploadingStatusEnum.UPLOAD_SECONDS.getCode());
                    fileInfoDAO.insert(dbFile);

                    updateUserSpace(userId, dbFile.getFileSize(), userSpaceDTO);

                    return resultDTO;
                }
            }


            //判断磁盘空间  逻辑可能有问题，应该判断直接整个文件大小，并且先把这部分空间预留
            Long currentTempSize = redisCache.getFileTempSize(userId, fileId);
            if (file.getSize() + currentTempSize + userSpaceDTO.getUseSpace() > userSpaceDTO.getTotalSpace()) {
                throw new BusinessException("用户空间不足");
            }

            //暂存临时目录
            String tempFolderPath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_TEMP_FILE_PATH;
            String currentFolderName = userId + fileId;

            tempFolder = new File(tempFolderPath + currentFolderName);
            if (!tempFolder.exists() && !tempFolder.mkdirs()) {
                throw new BusinessException("文件上传错误");
            }
            File chunkFile = new File(tempFolder.getPath() + File.separator + chunkIndex);
            file.transferTo(chunkFile);

            if (chunkIndex < chunks - 1) {
                resultDTO.setUploadingStatus(UploadingStatusEnum.UPLOADING.getCode());
                redisCache.saveFileTempSize(userId, fileId, file.getSize());
                return resultDTO;
            }

            //最后一个分片上传完成
            String month = DateUtil.formatTime(LocalDateTime.now(), DateUtil.YYYYMM);
            String fileSuffix = getFileSuffix(fileName);
            //真实文件名
            String realFileName = currentFolderName + fileSuffix;
            FileTypeEnum fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            //自动重命名
            fileName = autoRename(parentId, userId, fileName);
            //获取文件总大小
            Long fileTempSize = redisCache.getFileTempSize(userId, fileId) + file.getSize();

            FileInfoDO fileInfoDO = new FileInfoDO();
            fileInfoDO.setFileId(fileId);
            fileInfoDO.setUserId(userId);
            fileInfoDO.setMd5(md5);
            fileInfoDO.setFileSize(fileTempSize);
            fileInfoDO.setFileName(fileName);
            fileInfoDO.setFilePath(month + File.separator + realFileName);
            fileInfoDO.setParentId(parentId);
            fileInfoDO.setFileCategory(fileTypeEnum.getFileCategoryEnum().getCode());
            fileInfoDO.setFileType(fileTypeEnum.getCode());
            fileInfoDO.setStatus(FileStatusEnum.CODING.getCode());
            fileInfoDO.setFolderType(FolderTypeEnum.FILE.getCode());
            fileInfoDO.setDelFlag(DelFlagEnum.NORMAL.getCode());
            fileInfoDAO.insert(fileInfoDO);

            Long beforeUseSpace = userSpaceDTO.getUseSpace();
            updateUserSpace(userId, fileTempSize, userSpaceDTO);

            resultDTO.setUploadingStatus(UploadingStatusEnum.UPLOAD_FINISH.getCode());

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    if (UploadingStatusEnum.UPLOAD_FINISH.getCode().equals(resultDTO.getUploadingStatus())) {
                        CompletableFuture.runAsync(() -> transferFile(userId, fileInfoDO.getFileId(), beforeUseSpace), asyncServiceExecutor);
                    }
                }
            });

            return resultDTO;
        } catch (BusinessException e) {
            log.error("文件上传失败", e);
            uploadSuccess = false;
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            uploadSuccess = false;
            throw new BusinessException("文件上传失败");
        } finally {
            if (!uploadSuccess && Objects.nonNull(tempFolder)) {
                try {
                    FileUtils.deleteDirectory(tempFolder);
                } catch (IOException e) {
                    log.error("删除临时目录失败", e);
                }
            }
        }

    }

    @Override
    public void getCover(String cover, HttpServletResponse response) {
        if (StringUtils.isEmpty(cover)) {
            return;
        }
        String suffix = getFileSuffix(cover);
        String filePath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_FILE_PATH + cover;
        suffix = suffix.replace(".", "");
        String contentType = "image/".concat(suffix);
        response.setContentType(contentType);
        response.setHeader("cache-Control", "max-age=360000");
        readFile(response, filePath);
    }

    @Override
    public void getVideoInfo(String fileId, String videoPath, HttpServletResponse response) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        getVideoInfo(fileId, videoPath, currentUser.getUserId(), response);
    }

    public void getVideoInfo(String fileId, String videoPath, String userId, HttpServletResponse response) {
        if (videoPath.endsWith(".ts")) {
            //videoPath: fileId_0001.ts
//            String[] tsName = fileId.split("_");
//            String realFileId = tsName[0];
            FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, userId);
            if (Objects.isNull(fileInfoDO)) {
                return;
            }
            //文件路径拼接
            String video_cut_folder_name = getFileNameNoSuffix(fileInfoDO.getFilePath());// 202107/userId+fileId/
            String filePath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_FILE_PATH + video_cut_folder_name + File.separator + videoPath;//完整的绝对路径
            readFile(response, filePath);
        } else {
            getFile(fileId, userId, response);
        }
    }

    @Override
    public void getFile(String fileId, HttpServletResponse response) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        getFile(fileId, currentUser.getUserId(), response);
    }

    @Override
    public FileInfoVO createFolder(String parentId, String folderName) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        Assert.notNull(currentUser, "token验证过的不为空");

        //检查目录名是否存在
        checkFileName(parentId, currentUser.getUserId(), folderName, FolderTypeEnum.FOLDER.getCode());

        FileInfoDO fileInfoDO = new FileInfoDO();
        fileInfoDO.setFileId(String.valueOf(snowFlakeIDGenerator.nextId()));
        fileInfoDO.setUserId(currentUser.getUserId());
        fileInfoDO.setParentId(parentId);
        fileInfoDO.setFileName(folderName);
        fileInfoDO.setFolderType(FolderTypeEnum.FOLDER.getCode());
        fileInfoDO.setStatus(FileStatusEnum.CODE_SUCCESS.getCode());
        fileInfoDO.setDelFlag(DelFlagEnum.NORMAL.getCode());
        int count = fileInfoDAO.insert(fileInfoDO);
        if (count < 1) {
            throw new BusinessException("创建目录失败");
        }

        return SourceTargetMapper.INSTANCE.toFileInfoVO(fileInfoDAO.selectByFileIdAndUserId(fileInfoDO.getFileId(), currentUser.getUserId()));
    }

    @Override
    public List<FileInfoVO> getFolderInfo(String path) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(currentUser.getUserId());
        query.setFileIdList(Arrays.asList(path.split("/")));
        query.setFolderType(FolderTypeEnum.FOLDER.getCode());
        return fileInfoDAO.queryListSort(query).stream().map(SourceTargetMapper.INSTANCE::toFileInfoVO).collect(Collectors.toList());
    }

    @Override
    public FileInfoVO rename(String fileId, String fileName) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        String userId = currentUser.getUserId();
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfoDO)) {
            throw new BusinessException("文件不存在");
        }

        checkFileName(fileInfoDO.getParentId(), userId, fileName, fileInfoDO.getFolderType());

        if (FolderTypeEnum.FILE.getCode().equals(fileInfoDO.getFolderType())) {
            String fileSuffix = getFileSuffix(fileInfoDO.getFileName());
            fileName = fileName + fileSuffix;
        }
        FileInfoDO updateFileInfo = new FileInfoDO();
        updateFileInfo.setFileId(fileInfoDO.getFileId());
        updateFileInfo.setUserId(userId);
        updateFileInfo.setFileName(fileName);
        updateFileInfo.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        int count = fileInfoDAO.update(updateFileInfo);
        if (count < 1) {
            throw new BusinessException("文件重命名失败");
        }

        FileInfoQuery query = new FileInfoQuery();
        query.setFileId(fileInfoDO.getFileId());
        query.setUserId(userId);
        query.setFileName(fileId);
        int selectCount = fileInfoDAO.queryCount(query);
        if (selectCount > 1) {
            throw new BusinessException("文件名" + fileName + "已存在");
        }
        fileInfoDO.setFileName(fileName);
        fileInfoDO.setUpdateTime(updateFileInfo.getUpdateTime());
        return SourceTargetMapper.INSTANCE.toFileInfoVO(fileInfoDO);
    }

    @Override
    public List<FileInfoVO> queryAllFolders(String parentId, String currentFileIds) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        Assert.notNull(currentUser, "当前用户不为空");
        String userId = currentUser.getUserId();

        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setParentId(parentId);
        query.setFolderType(FolderTypeEnum.FOLDER.getCode());
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        if (!StringUtils.isEmpty(currentFileIds)) {
            query.setExcludeFileIdList(Arrays.asList(currentFileIds.split(",")));
        }
        return fileInfoDAO.queryList(query).stream().map(SourceTargetMapper.INSTANCE::toFileInfoVO).collect(Collectors.toList());
    }

    @Override
    public void move(String fileIds, String parentId) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        Assert.notNull(currentUser, "经过认证，当前用户不为空");
        String userId = currentUser.getUserId();
        if (fileIds.equals(parentId)) {
            throw new BusinessException("参数错误");
        }
        if (!Constant.ZERO_STR.equals(parentId)) {
            FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(parentId, userId);
            if (fileInfoDO == null || !DelFlagEnum.NORMAL.getCode().equals(fileInfoDO.getDelFlag())) {
                throw new BusinessException("目标目录不存在");
            }
        }
        List<String> fileIdList = Arrays.asList(fileIds.split(","));

        FileInfoQuery query = new FileInfoQuery();
        query.setParentId(parentId);
        query.setUserId(userId);

        Map<String, FileInfoDO> targetFolderFileMap = fileInfoDAO.queryList(query).stream()
                .collect(Collectors.toMap(FileInfoDO::getFileName, Function.identity(), (pre, post) -> post));

        //查询选中的文件
        query = new FileInfoQuery();
        query.setFileIdList(fileIdList);
        query.setUserId(userId);
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);

        //将所选文件重命名
        fileInfoDOS.forEach(fileInfoDO -> {
            FileInfoDO updateFileInfo = new FileInfoDO();
            if (targetFolderFileMap.containsKey(fileInfoDO.getFileName())) {
                updateFileInfo.setFileName(rename(fileInfoDO.getFileName()));
            }
            updateFileInfo.setFileId(fileInfoDO.getFileId());
            updateFileInfo.setUserId(userId);
            updateFileInfo.setParentId(parentId);
            fileInfoDAO.update(updateFileInfo);
        });
    }

    @Override
    public String createDownloadURL(String fileId) {
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        Assert.notNull(currentUser, "通过认证当前用户不为空");
        String downloadURL = createDownloadURL(fileId, currentUser.getUserId());
        return downloadURL;
    }

    public String createDownloadURL(String fileId, String userId) {
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfoDO)) {
            throw new BusinessException("文件不存在");
        }
        if (FolderTypeEnum.FOLDER.getCode().equals(fileInfoDO.getFolderType())) {
            throw new BusinessException("不支持下载目录");
        }
        String code = UUID.randomUUID().toString();
        DownLoadFileDTO downLoadFileDTO = new DownLoadFileDTO();
        downLoadFileDTO.setDownloadCode(code);
        downLoadFileDTO.setFileId(fileInfoDO.getFileId());
        downLoadFileDTO.setFilePath(fileInfoDO.getFilePath());
        downLoadFileDTO.setFileName(fileInfoDO.getFileName());

        redisCache.saveDownloadCode(downLoadFileDTO);
        return code;
    }

    @Override
    public void downloadFile(String code, HttpServletResponse response) {
        DownLoadFileDTO dwFile = redisCache.getDownloadCode(code);
        if (Objects.isNull(dwFile)) {
            return;
        }
        //拼接文件路径
        try {
            String filePath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_FILE_PATH + dwFile.getFilePath();
            String fileName = dwFile.getFileName();
            response.setContentType("application/x-msdownload; charset=UTF-8");
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment:filename=\"" + fileName + "\"");
            readFile(response, filePath);
        } catch (Exception e) {
            log.error("文件下载错误", e);
            throw new BusinessException("文件下载错误");
        }
    }

    @Override
    public void removeFile2Recycle(String fileIds) {
        List<String> fileIdList = Arrays.asList(fileIds.split(","));

        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(currentUser.getUserId());
        query.setFileIdList(fileIdList);
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        List<FileInfoDO> fileInfoDOS = fileInfoDAO.queryList(query);
        if (fileInfoDOS.isEmpty()) {
            return;
        }
        List<String> delFieldIdList = new ArrayList<>();
        getAllFileInfo(delFieldIdList, fileInfoDOS);
        if (!delFieldIdList.isEmpty()) {
            fileInfoDAO.updateDelFlagBatch(delFieldIdList, currentUser.getUserId(),
                    DelFlagEnum.RECOVERY.getCode(), null, Timestamp.valueOf(LocalDateTime.now()), DelFlagEnum.NORMAL.getCode());
        }
    }

    /**
     * 查询所选的所有文件，主要是获取目录下的文件
     *
     * @param fileIdList  所有文件的ID
     * @param fileInfoDOS 当前层级的文件列表
     */
    private void getAllFileInfo(List<String> fileIdList, List<FileInfoDO> fileInfoDOS) {
        if (fileInfoDOS == null || fileInfoDOS.isEmpty() || fileIdList == null) {
            return;
        }
        fileInfoDOS.forEach(fileInfoDO -> {
            fileIdList.add(fileInfoDO.getFileId());
            if (FolderTypeEnum.FOLDER.getCode().equals(fileInfoDO.getFolderType())) {
                FileInfoQuery query = new FileInfoQuery();
                query.setUserId(fileInfoDO.getUserId());
                query.setParentId(fileInfoDO.getFileId());
                query.setDelFlag(DelFlagEnum.NORMAL.getCode());
                getAllFileInfo(fileIdList, fileInfoDAO.queryList(query));
            }
        });
    }


    /**
     * 检查同一目录下文件名是否存在
     *
     * @param parentId   父目录
     * @param userId     用户ID
     * @param fileName   文件名称
     * @param folderType 文件或目录
     */
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

    public void getFile(String fileId, String userId, HttpServletResponse response) {
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, userId);
        if (Objects.isNull(fileInfoDO)) {
            return;
        }

        String filePath = null;
        if (FileCategoryEnum.VIDEO.getCode().equals(fileInfoDO.getFileCategory())) {
            //文件路径拼接
            String m3u8_folder_name = getFileNameNoSuffix(fileInfoDO.getFilePath());// 202107/userId+fileId
            filePath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_FILE_PATH + m3u8_folder_name + File.separator + Constant.M3U8_NAME;//完整的绝对路径
        } else {
            filePath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_FILE_PATH + fileInfoDO.getFilePath();
        }
        readFile(response, filePath);
    }


    private void updateUserSpace(String userId, Long fileSize, UserSpaceDTO userSpaceDTO) {
        //更新用户使用空间
        int count = userInfoDAO.updateSpaceByUserId(userId, fileSize, null);
        if (count < 1) {
            throw new BusinessException("用户空间不足");
        }
        userSpaceDTO.setUseSpace(fileSize + userSpaceDTO.getUseSpace());
        redisCache.saveUseSpaceDTO(userId, userSpaceDTO);
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

    /**
     * 如果同一目录下已存在相同名称的文件，就自动重新命名
     *
     * @param parentId 所在目录ID
     * @param userId   用户ID
     * @param fileName 文件名称
     * @return 文件新名称
     */
    private String autoRename(String parentId, String userId, String fileName) {
        FileInfoQuery query = new FileInfoQuery();
        query.setUserId(userId);
        query.setParentId(parentId);
        query.setFileName(fileName);
        query.setDelFlag(DelFlagEnum.NORMAL.getCode());
        int count = fileInfoDAO.queryCount(query);
        if (count > 0) {
            fileName = getFileNameNoSuffix(fileName) + "_" + snowFlakeIDGenerator.nextId() + getFileSuffix(fileName);
        }
        return fileName;
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

    public void transferFile(String userId, String fileId, Long beforeUseSpace) {
        boolean transferSuccess = true;
        String targetFilePath = null;
        String cover = null;
        FileTypeEnum fileTypeEnum = null;
        FileInfoDO fileInfoDO = fileInfoDAO.selectByFileIdAndUserId(fileId, userId);
        try {
            if (fileInfoDO == null || !FileStatusEnum.CODING.getCode().equals(fileInfoDO.getStatus())) {
                return;
            }
            //临时目录
            String tempFolderPath = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_TEMP_FILE_PATH;
            String currentFolderName = userId + fileId;
            File tempFolder = new File(tempFolderPath + currentFolderName);
            String month = DateUtil.formatTime(LocalDateTime.now(), DateUtil.YYYYMM);
            String fileSuffix = getFileSuffix(fileInfoDO.getFileName());

            //目标目录
            String targetFolderName = appConfig.getProjectFolderPath() + Constant.FILE_FOLDER_FILE_PATH;
            File targetFolder = new File(targetFolderName + File.separator + month);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
            //真实的文件名
            String realFileName = currentFolderName + fileSuffix;
            targetFilePath = targetFolder.getPath() + File.separator + realFileName;// /file/202107/userId+fileId.mp4

            //合并文件
            unionFile(tempFolder.getPath(), targetFilePath, fileInfoDO.getFileName(), true);

            //视频文件切割
            fileTypeEnum = FileTypeEnum.getFileTypeBySuffix(fileSuffix);
            if (FileTypeEnum.VIDEO == fileTypeEnum) {
                cutFile4Video(fileId, targetFilePath);
                //视频生成缩略图
                cover = month + File.separator + currentFolderName + Constant.PNG_SUFFIX;
                String coverPath = targetFolderName + File.separator + cover;// /file/202107/userid+fileid.png
                createCover4Video(new File(targetFilePath), 150, new File(coverPath));
            } else if (FileTypeEnum.PICTURE == fileTypeEnum) {
                //生成缩略图
                cover = month + File.separator + realFileName.replace(".", "_.");
                String coverPath = targetFolderName + File.separator + cover;
                Boolean isCreated = createCovet4Pic(new File(targetFilePath), 150, new File(coverPath), false);
                if (!isCreated) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            log.error("转码失败，文件ID:{}，用户ID:{}", fileId, userId, e);
            transferSuccess = false;
        } finally {
            FileInfoDO updateFileInfo = new FileInfoDO();
            long fileSize = new File(targetFilePath).length();
            updateFileInfo.setFileSize(fileSize);  //真实文件大小
            updateFileInfo.setFileCover(cover);
            updateFileInfo.setStatus(transferSuccess ? FileStatusEnum.CODE_SUCCESS.getCode() : FileStatusEnum.CODE_FAIL.getCode());
            fileInfoDAO.updateFileInfoStatus(fileId, userId, updateFileInfo, FileStatusEnum.CODING.getCode());

            //更新使用空间
            UserSpaceDTO userSpaceDTO = redisCache.getUserSpaceDTO(userId);
            long midSpace = fileSize - userSpaceDTO.getUseSpace() + beforeUseSpace;
            updateUserSpace(userId, midSpace, userSpaceDTO);

            //todo 消息推送
            //消息推送，提醒前端重新查询文件状态
            FileInfoDO afterTransferFileInfo = fileInfoDAO.selectByFileIdAndUserId(fileId, userId);
            rocketMQTemplate.asyncSend(MQTopicConstant.TRANSFER_TOPIC, afterTransferFileInfo, new SendCallback() {
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

    private Boolean createCovet4Pic(File pic, int width, File targetFile, Boolean delSource) {
        try {
            BufferedImage src = ImageIO.read(pic);
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            if (srcWidth <= width) {
                return false;
            }
            compressImage(pic, width, targetFile, delSource);
            return true;
        } catch (Exception e) {
            log.error("图片生成缩略图失败", e);
            throw new BusinessException("图片生成缩略图失败");
        }
    }

    private void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        Process process = null;
        try {
            final String CMD_COMPRESS_IMAGE = "ffmpeg -i %s -vf scale=%d:-1 %s -y";
//            final String CMD_COMPRESS_IMAGE = "ffmpeg -i %s -vf scale=%d:%d %s -y"; //scale=width:height -1表示不压缩
            String compress_cmd = String.format(CMD_COMPRESS_IMAGE, sourceFile.getAbsolutePath(), width, targetFile.getAbsolutePath());
            process = Runtime.getRuntime().exec(compress_cmd);
            recordProcessResult(compress_cmd, process.getInputStream(), process.getErrorStream());
            process.waitFor();
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
        } catch (Exception e) {
            log.error("生成缩略图失败", e);
            throw new BusinessException("生成缩略图失败");
        }
    }


    private void createCover4Video(File sourceFile, Integer width, File targetFile) {
        Process process = null;
        try {
            final String CMD_COVER = "ffmpeg -i %s -y -vframes 1 -vf scale=%d:%d/a %s";
            String cover_cmd = String.format(CMD_COVER, sourceFile.getAbsolutePath(), width, width, targetFile.getAbsolutePath());
            process = Runtime.getRuntime().exec(cover_cmd);
            recordProcessResult(cover_cmd, process.getInputStream(), process.getErrorStream());
            process.waitFor();
        } catch (Exception e) {
            log.error("生成视频封面失败", e);
            throw new BusinessException("生成封面失败");
        }
    }

    private void cutFile4Video(String fileId, String videoFilePath) {
        //创建同名切片目录
        File tsFolder = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()) {
            tsFolder.mkdirs();
        }
        //通过ffmpeg切片
        //ffmpeg -y -i 0000.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb 0000/index.ts执行成功，那就是合并的问题
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CMD_CUT_TS = "ffmpeg -i  %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsFolder.getPath() + File.separator + Constant.TS_NAME;
        Process process = null;
        try {
            //生成ts文件
            String ts_cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
            process = Runtime.getRuntime().exec(ts_cmd);
            recordProcessResult(ts_cmd, process.getInputStream(), process.getErrorStream());
            process.waitFor();


            //生成索引文件.m3u8和切片.ts
            String m3u8_cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + File.separator + Constant.M3U8_NAME, tsFolder.getPath(), fileId);
            process = Runtime.getRuntime().exec(m3u8_cmd);
            recordProcessResult(m3u8_cmd, process.getInputStream(), process.getErrorStream());
            process.waitFor();

            //删除index.ts
//            new File(tsPath).delete();
        } catch (Exception e) {
            log.error("视频转换失败，文件路径：{}", videoFilePath, e);
            throw new BusinessException("视频转换失败");
        }
    }

    private void recordProcessResult(final String cmd, final InputStream inputStream, final InputStream errorStream) {
        CountDownLatch countDownLatch = new CountDownLatch(2);//分别是输出流和错误流
        CompletableFuture.runAsync(() -> {
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                StringBuffer result = new StringBuffer();
                String line = null;
                while ((line = inputReader.readLine()) != null) {
                    result.append(line);
                }
                inputStream.close();
                log.info("命令：{}执行完毕，执行结果：{}", cmd, result);
                countDownLatch.countDown();
            } catch (Exception e) {
                log.error("命令：{}执行失败", cmd, e);
                throw new RuntimeException();
            }
        }, asyncServiceExecutor).whenComplete((unused, throwable) -> {
            throw new RuntimeException();
        });
        CompletableFuture.runAsync(() -> {
            try {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream, "GBK"));
                StringBuffer err = new StringBuffer();
                String line = null;
                while ((line = errorReader.readLine()) != null) {
                    err.append(line);
                }
                errorStream.close();
                log.info("命令：{}执行完毕，错误信息：{}", cmd, err);
                countDownLatch.countDown();
            } catch (Exception e) {
                log.error("命令：{}执行失败", cmd);
            }
        }, asyncServiceExecutor).whenComplete((unused, throwable) -> {
            throw new RuntimeException();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("命令：{}执行失败", cmd);
        }
    }

    private void unionFile(String dirPath, String toFilePath, String fileName, boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }

        File[] fileList = dir.listFiles();
        if (Objects.isNull(fileList)) {
            throw new BusinessException("源文件不存在");
        }
        File targetFile = new File(toFilePath);
        RandomAccessFile writeFile = null;
        RandomAccessFile readFile = null;
        try {
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] bytes = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                File chunkFile = new File(dirPath + File.separator + i);
                readFile = new RandomAccessFile(chunkFile, "r");
                while ((len = readFile.read(bytes)) != -1) {
                    writeFile.write(bytes, 0, len);
                }
            }
        } catch (Exception e) {
            log.error("文件:{}转码失败", fileName, e);
        } finally {
            if (Objects.nonNull(writeFile)) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    log.error("文件IO异常", e);
                }
            }
            if (Objects.nonNull(readFile)) {
                try {
                    readFile.close();
                } catch (IOException e) {
                    log.error("文件IO异常", e);
                }
            }
            if (delSource && dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    log.error("删除源文件失败", e);
                }
            }
        }
    }

    private void readFile(HttpServletResponse response, String filePath) {
        FileInputStream fileInputStream = null;
        try (OutputStream outputStream = response.getOutputStream()) {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.flush();
            fileInputStream.close();
        } catch (Exception e) {
            throw new BusinessException("获取文件失败");
        }
    }

}
