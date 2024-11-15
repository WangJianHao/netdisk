package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.common.constant.Constant;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.dto.request.WebShareQueryFileRequest;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.dto.vo.WebShareVO;
import com.sen.netdisk.service.WebShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/15 16:18
 */
@RestController
@Slf4j
@Api
@AllArgsConstructor
@RequestMapping("/webShare")
public class WebShareController {

    private final WebShareService webShareService;

    @ApiOperation("提取码验证通过查询分享详细")
    @GetMapping("/getShareInfoAfterAuth")
    public SenCommonResponse<WebShareVO> getShareInfoAfterAuth(HttpSession session, @RequestParam("shareId") String shareId) {
        String attribute = (String) session.getAttribute(Constant.SHARE_CODE_KEY_PREFIX + shareId);
        log.info("AfterAuth sessionId:{},attribute:{}", session.getId(), attribute);
        if (Objects.isNull(attribute)) {
            return SenCommonResponse.success(null);
        }
        WebShareVO shareInfo = webShareService.getShareInfoAfterAuth(shareId);
        return SenCommonResponse.success(shareInfo);
    }

    @ApiOperation("获取分享信息")
    @GetMapping("/getShareInfo")
    public SenCommonResponse<WebShareVO> getShareInfo(@RequestParam("shareId") String shareId) {
        return SenCommonResponse.success(webShareService.getShareInfo(shareId));
    }

    @ApiOperation("校验提取码")
    @PostMapping("/checkShareCode")
    public SenCommonResponse<Boolean> checkShareCode(@RequestParam("shareId") String shareId,
                                                     @RequestParam("code") String code, HttpSession session) {
        Boolean checkSuccess = webShareService.checkShareCode(shareId, code);
        if (checkSuccess) {
            session.setAttribute(Constant.SHARE_CODE_KEY_PREFIX + shareId, shareId);
            log.info("checkShareCode sessionId:{},attribute,{}", session.getId(), session.getAttribute(Constant.SHARE_CODE_KEY_PREFIX + shareId));
        }
        return SenCommonResponse.success(checkSuccess);
    }

    @ApiOperation("查询分享文件列表")
    @PostMapping("/queryFileList")
    public SenCommonResponse<SenCommonPage<FileInfoVO>> queryFileList(@RequestBody WebShareQueryFileRequest request, HttpSession session) {
        checkShare(session, request.getShareId());
        SenCommonPage<FileInfoVO> fileInfoVOS = webShareService.queryFileList(request);
        return SenCommonResponse.success(fileInfoVOS);
    }

    @ApiOperation("获取目录信息")
    @GetMapping("/getFolderInfo")
    public SenCommonResponse<List<FileInfoVO>> getFolderInfo(@RequestParam("shareId") String shareId,
                                                             @RequestParam("path") String path,
                                                             HttpSession session) {
        checkShare(session, shareId);
        List<FileInfoVO> fileInfoVOs = webShareService.getFolderInfo(shareId, path);
        return SenCommonResponse.success(fileInfoVOs);
    }

    @ApiOperation("查询封面")
    @GetMapping("/getCover")
    public void getCover(@RequestParam(value = "cover") String cover,
                         @RequestParam(value = "shareId") String shareId,
                         HttpSession session,
                         HttpServletResponse response) {
        checkShare(session, shareId);
        webShareService.getCover(cover, response);
    }

    @ApiOperation("获取文件信息")
    @GetMapping("/getFile/{shareId}/{fileId}")
    public void getFile(@PathVariable("shareId") String shareId,
                        @PathVariable("fileId") String fileId,
                        HttpSession session,
                        HttpServletResponse response) {
        checkShare(session, shareId);
        webShareService.getFile(shareId, fileId, response);
    }

    @ApiOperation("获取视频流")
    @GetMapping("getVideoInfo/{shareId}/{fileId}/ts/{videoPath}")
    public void getVideoInfo(@PathVariable("shareId") String shareId,
                             @PathVariable("fileId") String fileId,
                             @PathVariable("videoPath") String videoPath,
                             HttpSession session,
                             HttpServletResponse response) {
        checkShare(session, shareId);
        webShareService.getVideoInfo(shareId, fileId, videoPath, response);
    }

    @ApiOperation("创建下载链接")
    @PostMapping("/createDownloadURL")
    public SenCommonResponse<String> createDownloadURL(@RequestParam("shareId") String shareId,
                                                       @RequestParam(value = "fileId") String fileId, HttpSession session) {
        checkShare(session, shareId);
        return SenCommonResponse.success(webShareService.createDownloadURL(shareId, fileId));
    }

    @ApiOperation(value = "下载文件", produces = "application/octet-stream")
    @GetMapping(value = "/download/{code}")
    public void download(@PathVariable String code, HttpServletResponse response) {
        webShareService.downloadFile(code, response);
    }

    @ApiOperation(value = "保存到我的网盘")
    @PostMapping(value = "/saveShare")
    public SenCommonResponse<?> saveShare(@RequestParam("shareId") String shareId,
                                          @RequestParam(value = "shareFileIds") String shareFileIds,
                                          @RequestParam(value = "targetFolderId") String targetFolderId,
                                          HttpSession session) {
        checkShare(session, shareId);
        webShareService.saveShare(shareId, shareFileIds, targetFolderId);
        return SenCommonResponse.success();
    }

    private void checkShare(HttpSession session, String shareId) {
        String attribute = (String) session.getAttribute(Constant.SHARE_CODE_KEY_PREFIX + shareId);
        if (Objects.isNull(attribute)) {
            throw new BusinessException("验证过期，请重新输入提取码");
        }
    }

}
