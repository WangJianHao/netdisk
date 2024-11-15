package com.sen.netdisk.task.core;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import java.lang.reflect.InvocationTargetException;

import static com.sen.netdisk.task.core.TaskExecuteUtils.executeMethod;

/**
 * 定时任务处理（禁止并发执行）
 * 禁止并发执行的任务执行类，也继承自AbstractQuartzJob，执行逻辑同QuartzJobExecution。
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/11 23:09
 */
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzTask {

    @Override
    protected void doExecute(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        executeMethod(jobExecutionContext);
    }
}
