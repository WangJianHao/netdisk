package com.sen.netdisk.common.utils;

import com.sen.netdisk.common.exception.BusinessException;
import com.sen.netdisk.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.sen.netdisk.common.utils.DateUtil.parsePatterns;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/13 22:05
 */
@Slf4j
@Component
public class RedisIDGenerator {

    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1648895200L;

    /**
     * 序列号的位数
     */
    private static final int COUNT_BITS = 32;

    private static final String DEFAULT_ID_KEY = "netdisk:id-key";

    private RedisService<String> redisService;


    // offset表示的是id的递增梯度值
    public Long nextId(String hashKey, Long offset) throws BusinessException {
        try {
            if (null == offset) {
                offset = 1L;
            }
            // 生成唯一id
            return redisService.increment(DEFAULT_ID_KEY, hashKey, offset);
        } catch (Exception e) {
            //若是出现异常就是用uuid来生成唯一的id值
            log.error("redis服务异常", e);
            throw new BusinessException("redis服务连接异常");
        }
    }

    public Long nextIdForTime(String hashKey, Long offset) throws BusinessException {
        try {
            if (null == offset) {
                offset = 1L;
            }
            // 1.生成时间戳
            LocalDateTime now = LocalDateTime.now();
            //	得到当前的秒数
            long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
            long timestamp = nowSecond - BEGIN_TIMESTAMP;

            // 2.生成序列号
            // 2.1.获取当前日期，精确到天
            String date = DateUtil.formatTime(now, parsePatterns[12]);
            // 2.2.自增长 （每天一个key）
            long count = redisService.increment("netdisk:id-key:" + date, hashKey, offset);

            // 3.拼接并返回
            return timestamp << COUNT_BITS | count;
        } catch (Exception e) {
            //若是出现异常就是用uuid来生成唯一的id值
            log.error("redis服务异常", e);
            throw new BusinessException("redis服务连接异常");
        }
    }

}
