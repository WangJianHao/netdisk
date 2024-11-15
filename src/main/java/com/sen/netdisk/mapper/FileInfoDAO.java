package com.sen.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.common.SenCommonPage;
import com.sen.netdisk.dto.query.FileInfoQuery;
import com.sen.netdisk.entity.FileInfoDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 13:04
 */
@Repository
public interface FileInfoDAO extends BaseMapper<FileInfoDO> {

    IPage<FileInfoDO> listFileInfoWithPage(Page<FileInfoDO> page, @Param("query") FileInfoQuery query);

    List<FileInfoDO> queryList(@Param("query") FileInfoQuery query);

    List<FileInfoDO> queryListSort(@Param("query") FileInfoQuery query);

    Integer queryCount(@Param("query") FileInfoQuery query);

    Long selectUseSpaceByUserId(@Param("userId") String userId);

    FileInfoDO selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    int updateFileInfoStatus(@Param("fileId") String fileId, @Param("userId") String userId,
                             @Param("fileInfoDO") FileInfoDO fileInfoDO,
                             @Param("oldStatus") Integer oldStatus);

    int updateFileInfoDelFlag(@Param("fileId") String fileId, @Param("userId") String userId,
                              @Param("fileInfoDO") FileInfoDO fileInfoDO,
                              @Param("oldDelFlag") Integer oldDelFlag);


    List<FileInfoDO> queryExpireFileList(@Param("delFlag") @NotNull Integer delFlag,
                                         @Param("expireDay") @NotNull Integer expireDay);


    int update(@Param("fileInfoDO") FileInfoDO updateFileInfo);

    int deleteByFileId(@Param("fileId") String fileId);

    int updateDelFlagBatch(@Param("fileIdList") List<String> fileIdList,
                           @Param("userId") String userId,
                           @Param("delFlag") Integer delFlag,
                           @Param("delTime") Timestamp delTime,
                           @Param("recoveryTime") Timestamp recoveryTime,
                           @Param("oldDelFlag") Integer oldDelFlag);

    int updateBatch(@Param("fileInfoList") List<FileInfoDO> fileInfoList,
                    @Param("oldDelFlag") Integer oldDelFlag);

    int deleteByUserId(@Param("userId") String userId);

    int insertBatch(@Param("fileInfoList") List<FileInfoDO> fileInfoList);
}
