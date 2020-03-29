package com.github.jaychenfe.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author jaychenfe
 */
public class PagedGridUtils {

    public static <T> PagedGridResult<T> setterPagedGrid(List<T> list, Integer page) {
        PageInfo<T> pageList = new PageInfo<>(list);
        PagedGridResult<T> grid = new PagedGridResult<>();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
