package com.sen.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sen.netdisk.cache.RedisCache;
import com.sen.netdisk.common.constant.Constant;
import com.sen.netdisk.common.constant.UserStatusEnum;
import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.common.utils.JwtTokenUtil;
import com.sen.netdisk.common.utils.RandomUtil;
import com.sen.netdisk.common.utils.SnowFlakeIDGenerator;
import com.sen.netdisk.converter.SourceTargetMapper;
import com.sen.netdisk.dto.MailDTO;
import com.sen.netdisk.dto.SenUserDetails;
import com.sen.netdisk.dto.SysSettingDTO;
import com.sen.netdisk.dto.request.LoginRequest;
import com.sen.netdisk.dto.request.RegisterUserRequest;
import com.sen.netdisk.dto.request.ResetPasswordRequest;
import com.sen.netdisk.dto.response.LoginResponse;
import com.sen.netdisk.dto.vo.UserInfoVO;
import com.sen.netdisk.entity.UserInfoDO;
import com.sen.netdisk.mapper.UserInfoDAO;
import com.sen.netdisk.service.AccountService;
import com.sen.netdisk.service.MailService;
import com.sen.netdisk.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 19:53
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final RedisCache redisCache;

    private final PasswordEncoder passwordEncoder;

    private final SnowFlakeIDGenerator snowFlakeIDGenerator;

    private final MailService mailService;

    private final RedisService<String> redisService;

    private final UserDetailsService userDetailsService;

    private final UserInfoDAO userInfoDAO;

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    public AccountServiceImpl(RedisCache redisCache, PasswordEncoder passwordEncoder,
                              SnowFlakeIDGenerator snowFlakeIDGenerator, MailService mailService,
                              RedisService<String> redisService, UserDetailsService userDetailsService,
                              UserInfoDAO userInfoDAO, JwtTokenUtil jwtTokenUtil) {
        this.redisCache = redisCache;
        this.passwordEncoder = passwordEncoder;
        this.snowFlakeIDGenerator = snowFlakeIDGenerator;
        this.mailService = mailService;
        this.redisService = redisService;
        this.userDetailsService = userDetailsService;
        this.userInfoDAO = userInfoDAO;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    public Boolean verifyAuthCode(String mail, String authCode) throws BusinessException {
        // 判断验证码是否相同
        String key = Constant.AUTH_CODE_KEY_PREFIX + mail;
        String authCodeInRedis = redisService.get(key);
        if (authCodeInRedis == null) {
            throw new BusinessException("验证码已过期，请重新发送");
        }
        if (!StringUtils.equals(authCodeInRedis, authCode)) {
            throw new BusinessException("验证码错误");
        }
        return true;
    }

    @Override
    public void generateMailAuthCode(String email) throws BusinessException {
        // 判断当前待发送邮箱是否已经有验证码
        String key = Constant.AUTH_CODE_KEY_PREFIX + email;
        String authCodeInRedis = redisService.get(key);
        if (Objects.nonNull(authCodeInRedis)) {
            throw new BusinessException("验证码已发送");
        }

        // 生成随机验证码
        String authCode = RandomUtil.getRandomStr(Constant.FOUR);

        SysSettingDTO sysSettingDTO = redisCache.getSysSettingDTO();

        MailDTO mailDTO = new MailDTO();
        mailDTO.setSubject(sysSettingDTO.getRegisterEmailTitle());
        mailDTO.setText(String.format(sysSettingDTO.getRegisterEmailContent(), authCode));
        //添加到缓存中
        redisService.setEx(key, authCode, Constant.TEN_MINUTE);
//        mailService.sendMail(mailDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterUserRequest request) throws BusinessException {
        String email = request.getEmail();

        UserInfoDO user = userInfoDAO.selectByEmail(email);
        if (Objects.nonNull(user)) {
            throw new BusinessException("该邮箱已被注册");
        }

        //验证邮箱验证码
        String mailCodeInCache = redisService.get(Constant.AUTH_CODE_KEY_PREFIX + email);
        if (Objects.isNull(mailCodeInCache)) {
            throw new BusinessException("邮箱验证码已经过期");
        }
        if (!StringUtils.equalsIgnoreCase(mailCodeInCache, request.getEmailCode())) {
            throw new BusinessException("邮箱验证码错误");
        }


        UserInfoDO userInfoDO = SourceTargetMapper.INSTANCE.convert(request);
        userInfoDO.setUserId(String.valueOf(snowFlakeIDGenerator.nextId()));
        //密码加密
        String password = passwordEncoder.encode(request.getPassword());
        userInfoDO.setPassword(password);

        userInfoDO.setStatus(UserStatusEnum.ENABLE.getCode());

        //初始化使用空间
        SysSettingDTO sysSettingDTO = redisCache.getSysSettingDTO();
        Integer userInitUseSpace = sysSettingDTO.getUserInitTotalSpace();
        userInfoDO.setTotalSpace(userInitUseSpace * Constant.MB);
        userInfoDO.setUseSpace(0L);

        //存入数据库
        userInfoDAO.insert(userInfoDO);
    }

    @Override
    public LoginResponse login(LoginRequest request, HttpSession session) {
        LoginResponse response = new LoginResponse();
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            SenUserDetails userDetails = (SenUserDetails) userDetailsService.loadUserByUsername(email);
            if (Objects.isNull(userDetails)) {
                throw new BusinessException("用户名不存在");
            }
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码错误");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(userDetails);
            redisService.setEx(token, token, jwtTokenUtil.getDefaultExpirationTime());

            UserInfoDO updateLogin = new UserInfoDO();
            updateLogin.setLastLoginTime(Timestamp.valueOf(LocalDateTime.now()));
            LambdaQueryWrapper<UserInfoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfoDO::getEmail, request.getEmail());
            userInfoDAO.update(updateLogin, queryWrapper);

            response.setToken(token);
            UserInfoVO userInfoVO = SourceTargetMapper.INSTANCE.convert(userDetails.getUserInfoDTO());
            Long useSpace = userInfoDAO.selectUseSpaceByUserId(userInfoVO.getUserId());
            userInfoVO.setUseSpace(useSpace);
            response.setUserInfoVO(userInfoVO);
//            String id = session.getId();
//            session.setAttribute("currentUserId", userInfoVO.getUserId());
//            log.info("login sessionId:{}", id);
        } catch (AuthenticationException e) {
            log.error("登录失败:{}", e.getMessage());
            throw new BusinessException("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常", e);
            throw new BusinessException("登录失败");
        }
        return response;
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) throws BusinessException {
        String email = request.getEmail();
        String password = request.getPassword();
        String emailCodeInCache = redisService.get(Constant.AUTH_CODE_KEY_PREFIX + email);
        if (!StringUtils.equalsIgnoreCase(emailCodeInCache, request.getEmailCode())) {
            throw new BusinessException("邮箱验证码错误");
        }

        String encryptPassword = passwordEncoder.encode(password);
        int count = userInfoDAO.updatePasswordByEmail(email, encryptPassword);
        if (count < 1) {
            throw new BusinessException("密码重置失败，请联系管理员");
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader(this.tokenHeader);
        String authToken = authHeader.substring(this.tokenHead.length() + 1);// The part after "Bearer "
        redisService.delete(authToken);
    }

}
