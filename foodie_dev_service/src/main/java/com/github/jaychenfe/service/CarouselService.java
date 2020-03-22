package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.Carousel;

import java.util.List;

/**
 * @author jaychenfe
 */
public interface CarouselService {

    /**
     * 查询所有轮播图列表
     *
     * @param isShow 是否显示
     * @return 轮播图列表
     */
    List<Carousel> queryAll(Integer isShow);

}
