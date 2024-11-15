package com.sen.netdisk.service;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.dto.UploadFileResultDTO;
import com.sen.netdisk.dto.request.LoadDataListRequest;
import com.sen.netdisk.dto.vo.FileInfoNode;
import com.sen.netdisk.dto.vo.FileInfoVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:09
 */
public interface FileInfoService {

    SenCommonPage<?> listFileInfoWithPage(LoadDataListRequest request);

    List<FileInfoNode> listFileInfo();

    UploadFileResultDTO uploadFile(String fileId, MultipartFile file, String fileName, String parentId,
                                   String md5, Integer chunkIndex, Integer chunks);

    void getCover(String cover, HttpServletResponse response);

    void getVideoInfo(String fileId, String videoPath, HttpServletResponse response);

    void getVideoInfo(String fileId, String videoPath, String userId, HttpServletResponse response);

    void getFile(String fileId, HttpServletResponse response);

    void getFile(String fileId, String userId, HttpServletResponse response);

    FileInfoVO createFolder(String parentId, String folderName);

    List<FileInfoVO> getFolderInfo(String path);

    FileInfoVO rename(String fileId, String fileName);

    List<FileInfoVO> queryAllFolders(String parentId, String currentFileIds);

    void move(String fileIds, String parentId);

    String createDownloadURL(String fileId);

    String createDownloadURL(String fileId, String userId);

    void downloadFile(String code, HttpServletResponse response);

    void removeFile2Recycle(String fileIds);
}
