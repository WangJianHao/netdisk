package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.dto.UploadFileResultDTO;
import com.sen.netdisk.dto.request.LoadDataListRequest;
import com.sen.netdisk.dto.vo.FileInfoNode;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.service.FileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:09
 */
@Api
@Slf4j
@RestController
@RequestMapping("/file")
public class FileInfoController {

    private final FileInfoService fileInfoService;


    public FileInfoController(FileInfoService fileInfoService) {
        this.fileInfoService = fileInfoService;
    }


    @ApiOperation("根据文件分类分页查询")
    @PostMapping("/loadDataListByCategory")
    public SenCommonResponse<SenCommonPage<?>> loadDataListByCategory(@RequestBody LoadDataListRequest request) {
        return SenCommonResponse.success(fileInfoService.listFileInfoWithPage(request));
    }

    @ApiOperation("查询全部文件(树形结构)")
    @GetMapping("/listFileInfo4Tree")
    public SenCommonResponse<List<FileInfoNode>> listFileInfo4Tree() {
        return SenCommonResponse.success(fileInfoService.listFileInfo());
    }

    @ApiOperation("查询目录信息")
    @GetMapping("/getFolderInfo")
    public SenCommonResponse<List<FileInfoVO>> getFolderInfo(@RequestParam(value = "path") String path) {
        return SenCommonResponse.success(fileInfoService.getFolderInfo(path));
    }

    @ApiOperation("查询所有目录")
    @GetMapping("/queryAllFolders")
    public SenCommonResponse<List<FileInfoVO>> queryAllFolders(@RequestParam("parentId") String parentId,
                                                               @RequestParam(value = "currentFileIds", required = false) String currentFileIds) {
        return SenCommonResponse.success(fileInfoService.queryAllFolders(parentId, currentFileIds));
    }

    @ApiOperation("上传文件")
    @PostMapping("/uploadFile")
    public SenCommonResponse<UploadFileResultDTO> uploadFile(@RequestParam(value = "fileId", required = false) String fileId,
                                                             MultipartFile file,
                                                             @RequestParam(value = "fileName") String fileName,
                                                             @RequestParam(value = "parentId") String parentId,
                                                             @RequestParam(value = "md5") String md5,
                                                             @RequestParam(value = "chunkIndex") Integer chunkIndex,
                                                             @RequestParam(value = "chunks") Integer chunks) {

        return SenCommonResponse.success(fileInfoService.uploadFile(fileId, file, fileName, parentId, md5, chunkIndex, chunks));
    }


    @ApiOperation("查询封面")
    @GetMapping("/getCover")
    public void getCover(@RequestParam(value = "cover") String cover, HttpServletResponse response) {
        fileInfoService.getCover(cover, response);
    }

    @ApiOperation("获取视频流")
    @GetMapping("/getVideoInfo/{fileId}/ts/{videoPath}")
    public void getVideoInfo(@PathVariable(value = "fileId") String fileId,
                             @PathVariable(value = "videoPath") String videoPath,
                             HttpServletResponse response, HttpSession session) {
        String id = session.getId();
        String currentUserId = (String) session.getAttribute("currentUserId");
        log.info("video sessionId:{}", id);
        if (Objects.isNull(currentUserId)) {
            throw new BusinessException("用户未登录");
        }
        fileInfoService.getVideoInfo(fileId, videoPath, currentUserId, response);
    }

    @ApiOperation("获取文件")
    @GetMapping("/getFile/{fileId}")
    public void getFile(@PathVariable(value = "fileId") String fileId, HttpServletResponse response, HttpSession session) {
        String currentUserId = (String) session.getAttribute("currentUserId");
        if (Objects.isNull(currentUserId)) {
            throw new BusinessException("用户未登录");
        }
        fileInfoService.getFile(fileId, currentUserId, response);
    }

    @ApiOperation("创建目录")
    @PostMapping("/createFolder")
    public SenCommonResponse<FileInfoVO> createFolder(@RequestParam(value = "parentId") String parentId, @RequestParam(value = "folderName") String folderName) {
        return SenCommonResponse.success(fileInfoService.createFolder(parentId, folderName));
    }

    @ApiOperation("文件重命名")
    @PostMapping("/rename")
    public SenCommonResponse<FileInfoVO> rename(@RequestParam(value = "fileId") String fileId,
                                                @RequestParam(value = "fileName") String fileName) {
        return SenCommonResponse.success(fileInfoService.rename(fileId, fileName));
    }

    @ApiOperation("移动文件")
    @PostMapping("/move")
    public SenCommonResponse<?> move(@RequestParam(value = "fileIds") String fileIds,
                                     @RequestParam(value = "parentId") String parentId) {
        fileInfoService.move(fileIds, parentId);
        return SenCommonResponse.success();
    }

    @ApiOperation("创建下载链接")
    @PostMapping("/createDownloadURL")
    public SenCommonResponse<String> createDownloadURL(@RequestParam(value = "fileId") String fileId) {
        return SenCommonResponse.success(fileInfoService.createDownloadURL(fileId));
    }

    @ApiOperation(value = "下载文件", produces = "application/octet-stream")
    @GetMapping(value = "/download/{code}")
    public void download(@PathVariable String code, HttpServletResponse response) {
        fileInfoService.downloadFile(code, response);
    }

    @ApiOperation(value = "将文件移入回收站")
    @PostMapping(value = "/removeFile")
    public SenCommonResponse<?> delFile(@RequestParam(value = "fileIds") @NotEmpty(message = "文件ID不能为空") String fileIds) {
        fileInfoService.removeFile2Recycle(fileIds);
        return SenCommonResponse.success();
    }

}
