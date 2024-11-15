package com.sen.netdisk.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 统一分页处理
 *
 * @author sensen
 * @date 2021-01-01
 */
public class SenCommonPage<T> implements IPage<T> {

    /**
     * 页号
     */
    private Long current;

    /**
     * 每页数据的数量
     */
    private Long size;

    /**
     * 总页数
     */
    private Long totalPage;

    /**
     * 总数
     */
    private Long total;

    /**
     * 当前页的数据
     */
    private List<T> records;

    public static <T> SenCommonPage<T> restPage(IPage<T> page) {
        SenCommonPage<T> result = new SenCommonPage<T>();
        result.setTotalPage(page.getPages());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setRecords(page.getRecords());
        return result;
    }

    public static <T> SenCommonPage<T> restPage(Page<T> page) {
        SenCommonPage<T> result = new SenCommonPage<>();
        result.setTotalPage((long) page.getTotalPages());
        result.setCurrent(page.getNumber());
        result.setSize(page.getSize());
        result.setTotal(page.getTotalElements());
        result.setRecords(page.getContent());
        return result;
    }

    @Override
    public List<OrderItem> orders() {
        return null;
    }

    @Override
    public List<T> getRecords() {
        return this.records;
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public IPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public IPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public IPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }


}
