package com.github.jaychenfe.mapper;

import com.github.jaychenfe.pojo.vo.ItemCommentVO;
import com.github.jaychenfe.pojo.vo.SearchItemsVO;
import com.github.jaychenfe.pojo.vo.ShopCartVO;
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

    /**
     * 搜索商品列表
     *
     * @param map 参数
     * @return 搜索商品结果
     */
    List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    /**
     * 根据三级分类id搜索商品列表
     *
     * @param map 参数
     * @return 搜索商品结果
     */
    List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap") Map<String, Object> map);


    /**
     * 根据规格id列表查找购物车信息
     *
     * @param specIdsList 规格id列表
     * @return 购物车信息
     */
    List<ShopCartVO> queryItemsBySpecIds(@Param("paramsList") List specIdsList);

    /**
     * 扣除库存
     *
     * @param specId 规格id
     * @param pendingCounts 扣除数量
     * @return 执行数量
     */
    int decreaseItemSpecStock(@Param("specId") String specId,
                              @Param("pendingCounts") int pendingCounts);

}
