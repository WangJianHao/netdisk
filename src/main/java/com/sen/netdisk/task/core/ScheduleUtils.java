package com.sen.netdisk.task.core;

import com.sen.netdisk.common.constant.ScheduleConcurrentEnum;
import com.sen.netdisk.common.constant.ScheduleConstants;
import com.sen.netdisk.common.exception.BusinessException;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static com.sen.netdisk.common.constant.ScheduleConstants.TASK_PARAMS;

/**
 * 任务调度的工具类，根据SysJob创建JobDetail和CronTrigger，调度任务。
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/11 23:47
 */
@Component
public class ScheduleUtils {

    /**
     * 得到quartz任务类
     *
     * @param sysTask 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzTaskClass(SysTask sysTask) {
        boolean isConcurrent = ScheduleConcurrentEnum.ENABLE.getCode().equals(sysTask.getConcurrent());
        return isConcurrent ? QuartzTaskExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 创建定时任务
     *
     * @param scheduler
     * @param task      任务
     * @throws SchedulerException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void createScheduleTask(Scheduler scheduler, SysTask task) throws SchedulerException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        //1.获取任务类型
        Class<? extends Job> jobClass = getQuartzTaskClass(task);

        // 构建job信息
        String cornExpression = task.getCronExpression();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(task.getJobId(), task.getJobGroup()).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cornExpression);

        //配置执行策略
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(task, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(task.getJobId(), task.getJobGroup())
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(TASK_PARAMS, task);

        // 执行调度任务
        scheduler.scheduleJob(jobDetail, trigger);

    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysTask task, CronScheduleBuilder cb) {
        switch (task.getMisfirePolicy()) {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new BusinessException("策略异常");
        }
    }

}
