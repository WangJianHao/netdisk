package com.sen.netdisk.service;

import com.sen.netdisk.common.utils.SpringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/12 17:41
 */
public class AsyncTaskExecutor<T> {

    public static void executeAsyncTask(Runnable runnable) {
        CompletableFuture.runAsync(runnable, (ThreadPoolTaskExecutor) SpringUtils.getBean("asyncServiceExecutor"));
    }

}
