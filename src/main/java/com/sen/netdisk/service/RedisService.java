package com.sen.netdisk.service;

/**
 * @description:
 * @author: sensen
 * @date: 2024/8/8 23:34
 */
public interface RedisService<V> {

    void delete(String... key);

    V get(String key);

    boolean set(String key, V value);

    boolean setEx(String key, V value, long time);

    Long increment(String key, V hashKey, Long offset);
}
