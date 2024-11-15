package com.sen.netdisk.task.core;

import com.sen.netdisk.common.constant.ScheduleConstants;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 22:26
 */
@Data
public class SysTask implements Serializable {
    /**
     * 定时任务ID
     */
    private String jobId;

    /**
     * 定时任务名称
     */
    private String jobName;

    /**
     * 定时任务组
     */
    private String jobGroup;

    /**
     * 目标bean名
     */
    private String beanTarget;

    /**
     * 目标bean的方法名
     */
    private String beanMethodTarget;

    /**
     * 执行表达式
     */
    private String cronExpression;

    /**
     * 是否并发
     * 0代表允许并发执行
     * 1代表不允许并发执行
     */
    private String concurrent;

    /**
     * 计划策略，默认执行一次
     */
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;
}
