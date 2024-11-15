package com.sen.netdisk.service;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.dto.request.WebShareQueryFileRequest;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.dto.vo.WebShareVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 16:28
 */
public interface WebShareService {

    WebShareVO getShareInfo(String shareId);

    WebShareVO getShareInfoAfterAuth(String shareId);

    Boolean checkShareCode(String shareId, String code);

    SenCommonPage<FileInfoVO> queryFileList(WebShareQueryFileRequest request);

    List<FileInfoVO> getFolderInfo(String shareId, String path);

    void getFile(String shareId, String fileId, HttpServletResponse response);

    void getVideoInfo(String shareId, String fileId, String videoPath, HttpServletResponse response);

    String createDownloadURL(String shareId, String fileId);

    void downloadFile(String code, HttpServletResponse response);

    void saveShare(String shareId, String shareFileIds, String targetFolderId);

    void getCover(String cover, HttpServletResponse response);
}
