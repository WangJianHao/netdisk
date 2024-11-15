package com.sen.netdisk.task.service.impl;

import com.sen.netdisk.common.constant.ScheduleConcurrentEnum;
import com.sen.netdisk.task.core.SysTask;
import com.sen.netdisk.task.core.ScheduleUtils;
import com.sen.netdisk.task.service.SysTaskService;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/12 0:32
 */
@Service
public class SysTaskServiceImpl implements SysTaskService {

    @Resource
    private Scheduler scheduler;

    /**
     * 模拟从数据库获取数据，这边偷懒了
     *
     * @return
     */
    public List<SysTask> initTaskList() {
        List<SysTask> list = new ArrayList<>();
        SysTask task = new SysTask();
        task.setJobId("1");
        task.setJobGroup("system");
        task.setConcurrent(ScheduleConcurrentEnum.DISABLE.getCode());
        task.setCronExpression("0 0 0/1 * * ? ");//每小时执行一次
        task.setBeanTarget("autoDeleteRecoveryFileTask");
        task.setBeanMethodTarget("handle");
        list.add(task);
        return list;
    }

    /**
     * 初始化定时任务
     *
     * @throws SchedulerException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostConstruct
    @Override
    public void init() throws SchedulerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        scheduler.clear();
        List<SysTask> list = initTaskList();
        for (int i = 0; i < list.size(); i++) {
            SysTask task = list.get(i);
            ScheduleUtils.createScheduleTask(scheduler, task);
        }
    }

    @Override
    public int insertJob(SysTask task) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ScheduleUtils.createScheduleTask(scheduler, task);
        return 1;
    }

    @Override
    public void run(SysTask task) throws SchedulerException {
        scheduler.triggerJob(JobKey.jobKey(task.getJobId(), task.getJobGroup()));
    }

    @Override
    public int updateJob(SysTask job) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // 判断是否存在
        JobKey jobKey = JobKey.jobKey(job.getJobId(), job.getJobGroup());
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleTask(scheduler, job);
        return 1;
    }

    @Override
    public int pauseJob(SysTask task) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(task.getJobId(), task.getJobGroup()));
        return 1;
    }

    @Override
    public int resumeJob(SysTask task) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(task.getJobId(), task.getJobGroup()));
        return 1;
    }

    @Override
    public int deleteJob(SysTask task) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(task.getJobId(), task.getJobGroup()));
        return 1;
    }

}
