package com.sen.netdisk.task;

import com.sen.netdisk.task.handle.AutoDeleteRecoveryFileTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 清除七天以上的回收站文件
 *
 * @description:
 * @author: sensen
 * @date: 2024/8/11 20:12
 */
@Component("autoDeleteRecoveryFileTask")
public class AutoDeleteRecoveryFileTask {

    private static Logger log = LoggerFactory.getLogger(AutoDeleteRecoveryFileTask.class);

    private String cron = "0 0 0/1 1/1 * ? ";

    private final AutoDeleteRecoveryFileTaskHandler autoDeleteRecoveryFileTaskHandler;

    public AutoDeleteRecoveryFileTask(AutoDeleteRecoveryFileTaskHandler autoDeleteRecoveryFileTaskHandler) {
        this.autoDeleteRecoveryFileTaskHandler = autoDeleteRecoveryFileTaskHandler;
    }

    public void handle() throws InterruptedException {
        Date date = new Date();
        log.info("autoDeleteRecoveryFileTask" + date + "开始");
        autoDeleteRecoveryFileTaskHandler.execute();
        log.info("autoDeleteRecoveryFileTask" + date + "结束");
    }

}
