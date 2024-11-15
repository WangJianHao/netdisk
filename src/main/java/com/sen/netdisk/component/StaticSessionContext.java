package com.sen.netdisk.component;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/24 2:44
 */
public class StaticSessionContext {

    private static ConcurrentMap<String, HttpSession> attributes = new ConcurrentHashMap<>();

    private StaticSessionContext() {

    }

    public static void addSession(HttpSession session) {
        if (session != null) {
            attributes.put(session.getId(), session);
        }
    }

    public static void removeSession(HttpSession session) {
        if (session != null) {
            attributes.remove(session.getId());
        }
    }

    public static HttpSession getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return attributes.get(sessionId);
    }


}
