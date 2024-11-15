package com.sen.netdisk.task.core;

import org.quartz.JobExecutionContext;

import java.lang.reflect.InvocationTargetException;

import static com.sen.netdisk.task.core.TaskExecuteUtils.executeMethod;

/**
 * 定时任务处理（允许并发执行）
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/11 22:42
 */
public class QuartzTaskExecution extends AbstractQuartzTask {

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        executeMethod(jobExecutionContext);
    }

}
