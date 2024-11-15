package com.sen.netdisk.service.impl;


import com.sen.netdisk.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Quartz定时任务操作实现类
 * Created by macro on 2020/9/27.
 */
@Slf4j
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private Scheduler scheduler;

    private String defaultGroup = "default_group";

    @Override
    public String scheduleJob(Class<? extends Job> jobClass, String jobName, String cronExpression, String data) {
        // 创建需要执行的任务
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, defaultGroup)
                .usingJobData("data", data)
                .build();
        //创建触发器，指定任务执行时间
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, defaultGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        //使用调度器进行任务调度
        try {
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("创建定时任务失败！");
        }
        return jobName;
    }

    @Override
    public Boolean cancelScheduleJob(String jobName) {
        boolean success = false;
        try {
            // 暂停触发器
            scheduler.pauseTrigger(new TriggerKey(jobName, defaultGroup));
            // 移除触发器中的任务
            scheduler.unscheduleJob(new TriggerKey(jobName, defaultGroup));
            // 删除任务
            scheduler.deleteJob(new JobKey(jobName, defaultGroup));
            success = true;
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return success;
    }
}
