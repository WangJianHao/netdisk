package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.vo.FileShareVO;
import com.sen.netdisk.entity.ShareInfoDO;
import com.sen.netdisk.service.ShareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 17:56
 */
@Api
@RestController
@RequestMapping("/share")
@AllArgsConstructor
public class ShareController {

    private final ShareService shareService;

    @ApiOperation("查询所有分享链接")
    @PostMapping("/queryShareList")
    public SenCommonResponse<SenCommonPage<FileShareVO>> queryShareList(@RequestBody PageRequest request) {
        return SenCommonResponse.success(shareService.queryShareList(request));
    }

    @ApiOperation("创建分享链接")
    @PostMapping("/shareFile")
    public SenCommonResponse<FileShareVO> shareFile(@RequestParam("fileId") String fileId,
                                                    @RequestParam("validType") Integer validType,
                                                    @RequestParam(value = "code", required = false) String code) {
        return SenCommonResponse.success(shareService.saveShare(fileId, validType, code));
    }

    @ApiOperation("取消分享")
    @PostMapping("/cancelShare")
    public SenCommonResponse<?> cancelShare(@RequestParam("shareIds") String shareIds) {
        shareService.cancelShare(shareIds);
        return SenCommonResponse.success();
    }


    @ApiOperation("测试")
    @GetMapping("/test")
    public SenCommonResponse<?> queryShareList() {
        shareService.test();
        return SenCommonResponse.success();
    }
}
