package com.sen.netdisk.service;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.vo.FileInfoNode;
import com.sen.netdisk.dto.vo.FileInfoVO;
import com.sen.netdisk.dto.vo.RecycleFileInfoVO;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 14:28
 */
public interface RecycleService {

    List<FileInfoNode> queryRecycleTree();

    SenCommonPage<RecycleFileInfoVO> queryRecycleList(PageRequest pageRequest);

    void recoverFile(String fileIds);

    void delFile(String fileIds);
}
