package com.github.jaychenfe.service;


import com.github.jaychenfe.pojo.Items;
import com.github.jaychenfe.pojo.ItemsImg;
import com.github.jaychenfe.pojo.ItemsParam;
import com.github.jaychenfe.pojo.ItemsSpec;
import com.github.jaychenfe.pojo.vo.CommentLevelCountsVO;
import com.github.jaychenfe.pojo.vo.ItemCommentVO;
import com.github.jaychenfe.pojo.vo.SearchItemsVO;
import com.github.jaychenfe.pojo.vo.ShopCartVO;
import com.github.jaychenfe.utils.PagedGridResult;

import java.util.List;

/**
 * @author jaychenfe
 */
public interface ItemService {

    /**
     * 根据商品ID查询详情
     *
     * @param itemId 商品id
     * @return 商品详情
     */
    Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片列表
     *
     * @param itemId 商品id
     * @return 商品图片列表
     */
    List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格
     *
     * @param itemId 商品id
     * @return 商品规格
     */
    List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     *
     * @param itemId 商品id
     * @return 商品规格
     */
    ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品的评价等级数量
     *
     * @param itemId 商品id
     * @return 评价等级数量
     */
    CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据商品id查询商品的评价（分页）
     *
     * @param itemId   商品id
     * @param level    评价等级
     * @param page     当前页
     * @param pageSize 每页大小
     * @return 分页评论
     */
    PagedGridResult<ItemCommentVO> queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     *
     * @param keywords 关键字
     * @param sort     排序方式
     * @param page     当前页
     * @param pageSize 每页大小
     * @return 商品搜索结果
     */
    PagedGridResult<SearchItemsVO> searchItems(String keywords, String sort, Integer page, Integer pageSize);


    /**
     * 根据分类id搜索商品列表
     *
     * @param catId    分类id
     * @param sort     排序方式
     * @param page     当前页
     * @param pageSize 每页大小
     * @return 商品搜索结果
     */
    PagedGridResult<SearchItemsVO> searchItems(Integer catId, String sort, Integer page, Integer pageSize);

    /**
     * 根据规格ids查询最新的购物车中商品数据（用于刷新渲染购物车中的商品数据）
     *
     * @param specIds 规格id拼接
     * @return 购物车信息
     */
    List<ShopCartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id获取规格对象的具体信息
     *
     * @param specId 商品规格id
     * @return 商品规格
     */
    ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品id获得商品图片主图url
     *
     * @param itemId 商品id
     * @return 图片主图url
     */
    String queryItemMainImgById(String itemId);

    /**
     * 减少库存
     *
     * @param specId    商品规格id
     * @param buyCounts 购买数量
     */
    void decreaseItemSpecStock(String specId, int buyCounts);

}
