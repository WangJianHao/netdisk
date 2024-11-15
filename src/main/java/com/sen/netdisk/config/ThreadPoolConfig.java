package com.sen.netdisk.config;

import com.sen.netdisk.component.BusinessContextDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池
 *
 * @description: 网上有一个不成文的算法：CPU核心数量*2 +2 个线程。
 * @author: sensen
 * @date: 2024/8/8 21:39
 */
@Configuration
public class ThreadPoolConfig {

    private static final int CORE_THREAD_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    private static final int MAX_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;

    private static final int WORK_QUEUE_CAPACITY = 1000;

    private static final int KEEP_ALIVE_SECONDS = 60;


    @Bean("commonThreadPoolTaskExecutor")
    public Executor taskPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_THREAD_SIZE);
        executor.setMaxPoolSize(MAX_THREAD_SIZE);
        executor.setQueueCapacity(WORK_QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix("task-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setAllowCoreThreadTimeOut(true);
        executor.setTaskDecorator(new BusinessContextDecorator());
        executor.initialize();
        return executor;
    }

    @Bean(name = "asyncServiceExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {
        //在这里修改
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(10);
        //配置最大线程数
        executor.setMaxPoolSize(100);
        //设置线程空闲等待时间 s
        executor.setKeepAliveSeconds(20);
        //配置队列大小 设置任务等待队列的大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        //设置线程池内线程名称的前缀-------阿里编码规约推荐--方便出错后进行调试
        executor.setThreadNamePrefix("async-thread-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setTaskDecorator(new BusinessContextDecorator());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
