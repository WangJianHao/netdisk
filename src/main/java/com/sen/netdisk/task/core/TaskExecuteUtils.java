package com.sen.netdisk.task.core;

import com.sen.netdisk.common.utils.SpringUtils;
import org.quartz.JobExecutionContext;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.sen.netdisk.common.constant.ScheduleConstants.TASK_PARAMS;

/**
 * 执行定时任务的类
 * 任务执行的工具类，通过反射调用目标Bean的方法。
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/11 22:38
 */
public class TaskExecuteUtils {

    /**
     * 获取bean并执行对应的方法
     *
     * @param jobExecutionContext
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void executeMethod(JobExecutionContext jobExecutionContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object param = jobExecutionContext.getMergedJobDataMap().get(TASK_PARAMS);
        SysTask sysTask = new SysTask();
        BeanUtils.copyProperties(param, sysTask);
        Object bean = SpringUtils.getBean(sysTask.getBeanTarget());
        Method method = bean.getClass().getMethod(sysTask.getBeanMethodTarget());
        method.invoke(bean);
    }
}
