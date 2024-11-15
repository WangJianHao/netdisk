package com.sen.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sen.netdisk.dto.UserSpaceDTO;
import com.sen.netdisk.dto.query.UserInfoQuery;
import com.sen.netdisk.entity.UserInfoDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/7 16:02
 */
@Repository
public interface UserInfoDAO extends BaseMapper<UserInfoDO> {

    UserInfoDO selectByEmail(@Param("email") String email);

    int updatePasswordByEmail(@Param("email") String email, @Param("password") String password);

    int updateHeadIconByUserId(@Param("userId") String userId, @Param("headIcon") String headIcon);

    Long selectUseSpaceByUserId(@Param("userId") String userId);

    UserSpaceDTO selectSpaceByUserId(@Param("userId") String userId);

    int updateSpaceByUserId(@Param("userId") String userId, @Param("useSpace") Long useSpace, @Param("totalSpace") Long totalSpace);

    int updateUseSpaceByUserId(@Param("userId") String userId, @Param("useSpace") Long useSpace);

    Page<UserInfoDO> queryPage(IPage<UserInfoDO> page,@Param("query") UserInfoQuery query);

    int updateStatusById(@Param("userId") String userId, @Param("status") Integer status);

    UserInfoDO selectByUserId(@Param("userId") String shareUserId);
}
