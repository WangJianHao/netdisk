package com.sen.netdisk.config;

import com.sen.netdisk.component.TestHandleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/10 12:18
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TestHandleInterceptor testHandleInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testHandleInterceptor);
    }
}
