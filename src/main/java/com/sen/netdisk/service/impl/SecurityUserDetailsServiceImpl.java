package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.SenUserDetails;
import com.sen.netdisk.dto.UserInfoDTO;
import com.sen.netdisk.entity.UserInfoDO;
import com.sen.netdisk.mapper.UserInfoDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Author:  sensen
 * Date:  2024/7/29 17:13
 */
@Slf4j
@Service
@AllArgsConstructor
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    private UserInfoDAO umsAdminDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoDO::getEmail, username);
        UserInfoDO userInfoDO = umsAdminDAO.selectOne(queryWrapper);
        if (null == userInfoDO) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        UserInfoDTO userInfoDTO = SourceTargetMapper.INSTANCE.convert(userInfoDO);
        userInfoDTO.setIsAdmin(true);
        return new SenUserDetails(userInfoDTO);
    }

}
