package com.sen.netdisk.task.core;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.InvocationTargetException;

/**
 * 抽象QuartzJob类，实现了execute方法，在执行任务前后做了trycatch。
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/11 22:41
 */
public abstract class AbstractQuartzTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            doExecute(jobExecutionContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void doExecute(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}
