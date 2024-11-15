package com.sen.netdisk.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * MyBatis配置类
 *
 * @author sensen
 * @date 2021-01-01
 */
@Configuration
@MapperScan("com.sen.netdisk.mapper")
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

//    @Bean
//    public DatabaseIdProvider databaseIdProvider() {
//        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
//        Properties props = new Properties();
//        props.setProperty("Oracle", "oracle");
//        props.setProperty("MySQL", "mysql");
//        provider.setProperties(props);
//        return provider;
//    }

}

