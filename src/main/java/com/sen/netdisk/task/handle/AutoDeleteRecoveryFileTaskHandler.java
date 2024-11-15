package com.sen.netdisk.task.handle;

import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.constant.DelFlagEnum;
import com.sen.netdisk.entity.FileInfoDO;
import com.sen.netdisk.mapper.FileInfoDAO;
import lombok.AllArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/13 0:53
 */
@Component
@AllArgsConstructor
public class AutoDeleteRecoveryFileTaskHandler {

    private final FileInfoDAO fileInfoDAO;

    private final RedisCache redisCache;

    private final TransactionTemplate transactionTemplate;

    private final SqlSessionFactory sqlSessionFactory;

    //后面需要加到系统设置表中
    private static final Integer RECOVERY_EXPIRE_DAY = 30;

    private static final Integer DEL_FLAG_EXPIRE_DAY = 7;

    public void execute() {
        //清除删除标记超期的文件
        //清除回收站中超期的文件
        List<FileInfoDO> expireRecoveryFiles = fileInfoDAO.queryExpireFileList(DelFlagEnum.RECOVERY.getCode(), RECOVERY_EXPIRE_DAY);
        List<FileInfoDO> expireDelFiles = fileInfoDAO.queryExpireFileList(DelFlagEnum.DELETE.getCode(), DEL_FLAG_EXPIRE_DAY);
        transactionTemplate.executeWithoutResult(transactionStatus -> {

            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            FileInfoDAO mapper = sqlSession.getMapper(FileInfoDAO.class);
            expireRecoveryFiles.forEach(fileInfoDO -> {
                fileInfoDO.setDelFlag(DelFlagEnum.DELETE.getCode());
                fileInfoDO.setDelTime(Timestamp.valueOf(LocalDateTime.now()));
                mapper.updateFileInfoDelFlag(fileInfoDO.getFileId(), fileInfoDO.getUserId(), fileInfoDO, DelFlagEnum.RECOVERY.getCode());
            });

            expireDelFiles.forEach(fileInfoDO -> {
                mapper.deleteByFileId(fileInfoDO.getFileId());
            });
            sqlSession.commit();
        });
        if (!expireDelFiles.isEmpty()) {
            redisCache.reloadUserSpace(expireDelFiles.stream().map(FileInfoDO::getUserId).collect(Collectors.toList()));
        }
    }

}
