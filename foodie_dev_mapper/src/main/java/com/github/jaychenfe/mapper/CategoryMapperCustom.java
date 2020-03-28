package com.github.jaychenfe.mapper;


import com.github.jaychenfe.pojo.vo.CategoryVO;
import com.github.jaychenfe.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jaychenfe
 */
public interface CategoryMapperCustom {

    /**
     * 获取二级分类列表
     * @param rootCatId 一级分类id
     * @return 二级分类列表
     */
    List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 获取最新商品列表
     * @param map 参数
     * @return 最新商品列表
     */
    List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}
