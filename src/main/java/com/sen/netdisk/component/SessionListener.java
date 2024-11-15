package com.sen.netdisk.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import java.util.Enumeration;

/**
 * spring security为了防止固定回话攻击会一直修改sessionId,
 * 所以在登录前存在session里的数据在登录后是获取不到的。
 * 为了解决这种情况可以监听session的变化做相应的更改。
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/24 2:42
 */
@Slf4j
@WebListener
@Component
public class SessionListener implements HttpSessionListener, HttpSessionIdListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        StaticSessionContext.addSession(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        StaticSessionContext.removeSession(session);
    }

    /**
     * 在该方法中可以将原来session的数据移到新的session中
     *
     * @param se           the notification event
     * @param oldSessionId the old session ID
     */
    @Override
    public void sessionIdChanged(HttpSessionEvent se, String oldSessionId) {
        HttpSession oldSession = StaticSessionContext.getSession(oldSessionId);
        log.info("oldSessionId:{},newSessionId:{}", oldSessionId, se.getSession().getId());
        if (oldSession != null) {
            //C86CBD9317AF59B4C513EE56FBF727C1
            StaticSessionContext.removeSession(oldSession);
            Enumeration<String> attributeNames = oldSession.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                se.getSession().setAttribute(name, oldSession.getAttribute(name));
                log.info("oldSessionId:{},attribute:{}", oldSessionId, name);
                log.info("newSessionId:{},attribute:{}", se.getSession().getId(), name);
            }

        }
        StaticSessionContext.addSession(se.getSession());
    }
}
