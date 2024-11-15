package com.sen.netdisk.component;

import com.sen.netdisk.dto.UserInfoDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/10 11:57
 */
@Component
public class TestHandleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserInfoDTO currentUser = SenUserContextHolder.getCurrentUser();
        if (Objects.nonNull(currentUser)) {
            session.setAttribute("currentUserId", currentUser.getUserId());
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
