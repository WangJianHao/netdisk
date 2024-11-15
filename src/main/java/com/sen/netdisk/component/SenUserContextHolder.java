package com.sen.netdisk.component;

import com.sen.netdisk.dto.SenUserDetails;
import com.sen.netdisk.dto.UserInfoDTO;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 2:28
 */
public class SenUserContextHolder {

    private static final ThreadLocal<UserInfoDTO> userHolder = new ThreadLocal<>();

    public static UserInfoDTO getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SenUserDetails) {
            SenUserDetails principal = (SenUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal.getUserInfoDTO();
        }
        return null;
//        return userHolder.get();
    }

    public static void setCurrentUser(UserInfoDTO user) {
        userHolder.set(user);
    }

    public static void clear() {
        userHolder.remove();
    }
}
