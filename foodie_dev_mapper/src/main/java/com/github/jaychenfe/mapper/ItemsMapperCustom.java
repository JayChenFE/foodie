package com.github.jaychenfe.mapper;

import com.github.jaychenfe.pojo.vo.ItemCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jaychenfe
 */
public interface ItemsMapperCustom {

    /**
     * 查询商品评论列表
     *
     * @param map 参数
     * @return 商品评论列表
     */
    List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

}
