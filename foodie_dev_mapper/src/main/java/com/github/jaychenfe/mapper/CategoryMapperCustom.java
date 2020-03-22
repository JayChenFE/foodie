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

    List<CategoryVO> getSubCatList(Integer rootCatId);

    List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String, Object> map);
}
