package com.sen.netdisk.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/14 13:39
 */
@Slf4j
@Component
public class CacheLoader implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("缓存初始化开始--------------");
        log.info("缓存初始化结束--------------");
    }

}
