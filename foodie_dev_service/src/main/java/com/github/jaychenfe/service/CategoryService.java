package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.Category;
import com.github.jaychenfe.pojo.vo.CategoryVO;
import com.github.jaychenfe.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {

    /**
     * 查询所有一级分类
     *
     * @return 所有一级分类
     */
    List<Category> queryAllRootLevelCat();

    /**
     * 根据一级分类id查询子分类信息
     *
     * @param rootCatId 一级分类id
     * @return 子分类信息列表
     */
    List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页每个一级分类下的6条最新商品数据
     *
     * @param rootCatId 一级分类id
     * @return 6条最新商品数据
     */
    List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);

}
