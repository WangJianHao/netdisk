package com.sen.netdisk.component;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 21:51
 */
@Component
public class BusinessContextDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            if (Objects.nonNull(map)) {
                MDC.setContextMap(map);
            }
            runnable.run();
        };
    }
}
