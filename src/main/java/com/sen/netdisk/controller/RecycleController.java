package com.sen.netdisk.controller;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.vo.FileInfoNode;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.dto.vo.RecycleFileInfoVO;
import com.sen.netdisk.service.RecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 14:27
 */
@Api
@RestController
@AllArgsConstructor
@RequestMapping("recycle")
public class RecycleController {

    private final RecycleService recycleService;

    @ApiOperation("查询回收站文件")
    @PostMapping("/queryRecycleList")
    public SenCommonResponse<SenCommonPage<RecycleFileInfoVO>> queryRecycleList(@RequestBody PageRequest pageRequest) {
        SenCommonPage<RecycleFileInfoVO> list = recycleService.queryRecycleList(pageRequest);
        return SenCommonResponse.success(list);
    }

    @ApiOperation("查询回收站文件（树形）")
    @GetMapping("/queryRecycleTree")
    public SenCommonResponse<List<FileInfoNode>> queryRecycleTree() {
        return SenCommonResponse.success(recycleService.queryRecycleTree());
    }


    @ApiOperation("恢复文件")
    @PostMapping("/recoverFile")
    public SenCommonResponse<?> recoverFile(@RequestParam("fileIds") String fileIdS) {
        recycleService.recoverFile(fileIdS);
        return SenCommonResponse.success();
    }

    @ApiOperation("删除文件")
    @PostMapping("/delFile")
    public SenCommonResponse<?> delFile(@RequestParam("fileIds") String fileIds) {
        recycleService.delFile(fileIds);
        return SenCommonResponse.success();
    }
}
