package com.github.jaychenfe.utils;

import java.util.List;

/**
 * @author jaychenfe
 * @Title: PagedGridResult.java
 * @Package com.github.jaychenfe.utils
 * @Description: 用来返回分页Grid的数据格式
 */

public class PagedGridResult<T> {

    /**
     * 当前页数
     */
    private int page;
    /**
     * 总页数
     */
    private int total;
    /**
     * 总记录数
     */
    private long records;
    /**
     * 当页显示的内容
     */
    private List<T> rows;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
