package com.sen.netdisk.task.service;

import com.sen.netdisk.task.core.SysTask;
import org.quartz.SchedulerException;

import java.lang.reflect.InvocationTargetException;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/12 0:30
 */
public interface SysTaskService {

    /**
     * 项目启动时，初始化定时器
     */
    void init() throws SchedulerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * 新增任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int insertJob(SysTask job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     * @return 结果
     */
    void run(SysTask job) throws SchedulerException;

    /**
     * 更新任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int updateJob(SysTask job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int pauseJob(SysTask job) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int resumeJob(SysTask job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    int deleteJob(SysTask job) throws SchedulerException;
}
