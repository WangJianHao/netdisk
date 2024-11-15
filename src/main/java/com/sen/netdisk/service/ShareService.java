package com.sen.netdisk.service;

import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.dto.request.PageRequest;
import com.sen.netdisk.dto.vo.FileShareVO;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 18:05
 */
public interface ShareService {
    SenCommonPage<FileShareVO> queryShareList(PageRequest request);

    void test();

    FileShareVO saveShare(String fileId, Integer validType, String code);

    void cancelShare(String shareIds);
}
