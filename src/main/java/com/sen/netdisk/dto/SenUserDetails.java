package com.sen.netdisk.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/10 0:50
 */
@Data
public class SenUserDetails implements UserDetails {

    private UserInfoDTO userInfoDTO;

    public SenUserDetails(UserInfoDTO userInfoDTO) {
        this.userInfoDTO = userInfoDTO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return userInfoDTO.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfoDTO.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
