package com.sen.netdisk.common.constant;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/11 20:56
 */
public class ScheduleConstants {

    /**
     * 参数
     */
    public static final String TASK_PARAMS = "PARAMS";

    /**
     * 默认 等价MISFIRE_FIRE_AND_PROCEED
     */
    public static final String MISFIRE_DEFAULT = "0";

    /**
     * 一次性执行错过的,然后按新 Cron 继续运行。
     */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /**
     * 补偿错过的执行,然后继续运行。
     */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /**
     * 错过的执行不做任何处理,等待下一次 Cron 触发。
     */
    public static final String MISFIRE_DO_NOTHING = "3";
}
