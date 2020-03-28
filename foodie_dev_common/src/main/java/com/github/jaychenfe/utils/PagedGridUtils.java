package com.github.jaychenfe.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author jaychenfe
 */
public class PagedGridUtils {

    public static PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
