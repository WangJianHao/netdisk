package com.sen.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.dto.FileShareDTO;
import com.sen.netdisk.dto.query.ShareQuery;
import com.sen.netdisk.entity.ShareInfoDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 18:10
 */
@Repository
public interface ShareInfoDAO extends BaseMapper<ShareInfoDO> {


    int deleteBatch(@Param("userId") String userId, @Param("shareIdList") List<String> shareIdList);

    Page<FileShareDTO> selectPage(IPage<FileShareDTO> page, @Param("query") ShareQuery query);

    ShareInfoDO selectByShareId(@Param("shareId") String shareId);

    int updateShareCount(@Param("shareId") String shareId);
}
