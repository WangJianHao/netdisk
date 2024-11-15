package com.sen.netdisk.service;

import org.quartz.Job;

import java.util.Date;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/5 20:40
 */
public interface ScheduleService {

    /**
     * 通过CRON表达式调度任务
     */
    String scheduleJob(Class<? extends Job> jobClass,String jobName, String cron, String data);


    /**
     * 取消定时任务
     */
    Boolean cancelScheduleJob(String jobName);
}
