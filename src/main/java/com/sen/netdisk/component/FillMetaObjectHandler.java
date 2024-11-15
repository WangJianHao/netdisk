package com.sen.netdisk.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Author:  sensen
 * Date:  2024/7/28 20:54
 */
@Slf4j
@Component
public class FillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", Timestamp.valueOf(LocalDateTime.now()), metaObject);
        this.setFieldValByName("updateTime", Timestamp.valueOf(LocalDateTime.now()), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", Timestamp.valueOf(LocalDateTime.now()), metaObject);
    }
}
