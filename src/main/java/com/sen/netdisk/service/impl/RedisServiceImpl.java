package com.sen.netdisk.service.impl;

import com.sen.netdisk.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 23:27
 */
@Component
public class RedisServiceImpl<V> implements RedisService<V> {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceImpl.class);


    private final RedisTemplate<String, V> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值或多个
     */
    @Override
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    /**
     * 根据key获取缓存的值
     *
     * @param key key
     * @return 对应的值
     */
    @Override
    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 添加缓存
     *
     * @param key   key
     * @param value value
     * @return 成功或失败
     */
    @Override
    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置redis失败,key:{},value:{}", key, value, e);
            return false;
        }
    }

    /**
     * 设置超时销毁的缓存
     *
     * @param key   key
     * @param value value
     * @param time  超时时间，单位s
     * @return 成功或失败
     */
    @Override
    public boolean setEx(String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置redis失败,key:{},value:{},time:{}", key, value, time, e);
            return false;
        }
    }

    @Override
    public Long increment(String key, V hashKey, Long offset) {
        return redisTemplate.opsForHash().increment(key, hashKey, offset);
    }


}
